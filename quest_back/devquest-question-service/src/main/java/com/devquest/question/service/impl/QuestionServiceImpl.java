package com.devquest.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.devquest.common.dto.QuestionDTO;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.ResultCode;
import com.devquest.question.entity.Question;
import com.devquest.question.entity.QuestionTag;
import com.devquest.question.entity.Tag;
import com.devquest.question.mapper.QuestionMapper;
import com.devquest.question.mapper.QuestionTagMapper;
import com.devquest.question.mapper.TagMapper;
import com.devquest.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final QuestionTagMapper questionTagMapper;
    private final TagMapper tagMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String QUESTION_CACHE_KEY = "question:detail:";

    @Override
    @Transactional
    public Question createQuestion(Long userId, QuestionDTO dto) {
        Question question = new Question();
        question.setUserId(userId);
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setViewCount(0);
        question.setAnswerCount(0);
        question.setVoteCount(0);
        question.setFavoriteCount(0);
        question.setStatus(1);
        question.setIsTop(0);
        questionMapper.insert(question);

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            for (Long tagId : dto.getTagIds()) {
                QuestionTag questionTag = new QuestionTag();
                questionTag.setQuestionId(question.getId());
                questionTag.setTagId(tagId);
                questionTagMapper.insert(questionTag);

                LambdaUpdateWrapper<Tag> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Tag::getId, tagId)
                        .setSql("question_count = question_count + 1");
                tagMapper.update(null, updateWrapper);
            }
        }

        return question;
    }

    @Override
    public Question getQuestionDetail(Long questionId) {
        String cacheKey = QUESTION_CACHE_KEY + questionId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof Question) {
            return (Question) cached;
        }

        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BizException(ResultCode.QUESTION_NOT_FOUND);
        }

        redisTemplate.opsForValue().set(cacheKey, question, 3600, TimeUnit.SECONDS);
        return question;
    }

    @Override
    public PageResult<Question> getQuestionList(int page, int size, Long tagId, String sort) {
        Page<Question> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getStatus, 1);

        if (tagId != null) {
            LambdaQueryWrapper<QuestionTag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.eq(QuestionTag::getTagId, tagId);
            List<QuestionTag> questionTags = questionTagMapper.selectList(tagWrapper);
            List<Long> questionIds = questionTags.stream()
                    .map(QuestionTag::getQuestionId)
                    .collect(Collectors.toList());
            if (questionIds.isEmpty()) {
                return PageResult.of(List.of(), 0, page, size);
            }
            wrapper.in(Question::getId, questionIds);
        }

        if ("hot".equals(sort)) {
            wrapper.orderByDesc(Question::getVoteCount, Question::getViewCount);
        } else if ("unanswered".equals(sort)) {
            wrapper.eq(Question::getAnswerCount, 0);
            wrapper.orderByDesc(Question::getCreatedAt);
        } else {
            wrapper.orderByDesc(Question::getCreatedAt);
        }

        Page<Question> result = questionMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public PageResult<Question> getHotQuestions(int page, int size) {
        Page<Question> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getStatus, 1)
                .orderByDesc(Question::getVoteCount, Question::getViewCount);

        Page<Question> result = questionMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public PageResult<Question> getUserQuestions(Long userId, int page, int size) {
        Page<Question> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getUserId, userId)
                .orderByDesc(Question::getCreatedAt);

        Page<Question> result = questionMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    @Transactional
    public void updateQuestion(Long userId, Long questionId, QuestionDTO dto) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BizException(ResultCode.QUESTION_NOT_FOUND);
        }
        if (!question.getUserId().equals(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }

        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        questionMapper.updateById(question);

        LambdaQueryWrapper<QuestionTag> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(QuestionTag::getQuestionId, questionId);
        questionTagMapper.delete(deleteWrapper);

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            for (Long tagId : dto.getTagIds()) {
                QuestionTag questionTag = new QuestionTag();
                questionTag.setQuestionId(questionId);
                questionTag.setTagId(tagId);
                questionTagMapper.insert(questionTag);
            }
        }

        redisTemplate.delete(QUESTION_CACHE_KEY + questionId);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long userId, Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BizException(ResultCode.QUESTION_NOT_FOUND);
        }
        if (!question.getUserId().equals(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }

        question.setStatus(0);
        questionMapper.updateById(question);

        LambdaQueryWrapper<QuestionTag> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(QuestionTag::getQuestionId, questionId);
        questionTagMapper.delete(deleteWrapper);

        redisTemplate.delete(QUESTION_CACHE_KEY + questionId);
    }

    @Override
    public void incrementViewCount(Long questionId) {
        LambdaUpdateWrapper<Question> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Question::getId, questionId)
                .setSql("view_count = view_count + 1");
        questionMapper.update(null, updateWrapper);
        redisTemplate.delete(QUESTION_CACHE_KEY + questionId);
    }
}
