package com.devquest.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.devquest.common.dto.AnswerDTO;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.ResultCode;
import com.devquest.question.entity.Answer;
import com.devquest.question.entity.Question;
import com.devquest.question.mapper.AnswerMapper;
import com.devquest.question.mapper.QuestionMapper;
import com.devquest.question.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public Answer createAnswer(Long userId, AnswerDTO dto) {
        Question question = questionMapper.selectById(dto.getQuestionId());
        if (question == null) {
            throw new BizException(ResultCode.QUESTION_NOT_FOUND);
        }

        Answer answer = new Answer();
        answer.setQuestionId(dto.getQuestionId());
        answer.setUserId(userId);
        answer.setContent(dto.getContent());
        answer.setVoteCount(0);
        answer.setIsAccepted(0);
        answer.setIsAiGenerated(0);
        answer.setStatus(1);
        answerMapper.insert(answer);

        LambdaUpdateWrapper<Question> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Question::getId, dto.getQuestionId())
                .setSql("answer_count = answer_count + 1")
                .set(Question::getLastAnswerTime, LocalDateTime.now());
        questionMapper.update(null, updateWrapper);

        redisTemplate.delete("question:detail:" + dto.getQuestionId());
        return answer;
    }

    @Override
    public Answer getAnswerById(Long answerId) {
        Answer answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new BizException(ResultCode.ANSWER_NOT_FOUND);
        }
        return answer;
    }

    @Override
    public PageResult<Answer> getAnswersByQuestion(Long questionId, int page, int size, String sort) {
        Page<Answer> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getQuestionId, questionId)
                .eq(Answer::getStatus, 1);

        if ("votes".equals(sort)) {
            wrapper.orderByDesc(Answer::getVoteCount);
        } else if ("oldest".equals(sort)) {
            wrapper.orderByAsc(Answer::getCreatedAt);
        } else {
            wrapper.orderByDesc(Answer::getIsAccepted, Answer::getCreatedAt);
        }

        Page<Answer> result = answerMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    @Transactional
    public void updateAnswer(Long userId, Long answerId, String content) {
        Answer answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new BizException(ResultCode.ANSWER_NOT_FOUND);
        }
        if (!answer.getUserId().equals(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }

        answer.setContent(content);
        answerMapper.updateById(answer);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long userId, Long answerId) {
        Answer answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new BizException(ResultCode.ANSWER_NOT_FOUND);
        }
        if (!answer.getUserId().equals(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }

        answer.setStatus(0);
        answerMapper.updateById(answer);

        LambdaUpdateWrapper<Question> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Question::getId, answer.getQuestionId())
                .setSql("answer_count = answer_count - 1");
        questionMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional
    public void acceptAnswer(Long userId, Long questionId, Long answerId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BizException(ResultCode.QUESTION_NOT_FOUND);
        }
        if (!question.getUserId().equals(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }

        if (question.getAcceptedAnswerId() != null) {
            LambdaUpdateWrapper<Answer> resetWrapper = new LambdaUpdateWrapper<>();
            resetWrapper.eq(Answer::getId, question.getAcceptedAnswerId())
                    .set(Answer::getIsAccepted, 0);
            answerMapper.update(null, resetWrapper);
        }

        LambdaUpdateWrapper<Answer> acceptWrapper = new LambdaUpdateWrapper<>();
        acceptWrapper.eq(Answer::getId, answerId)
                .set(Answer::getIsAccepted, 1);
        answerMapper.update(null, acceptWrapper);

        question.setAcceptedAnswerId(answerId);
        question.setStatus(2);
        questionMapper.updateById(question);

        redisTemplate.delete("question:detail:" + questionId);
    }

    @Override
    public PageResult<Answer> getUserAnswers(Long userId, int page, int size) {
        Page<Answer> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getUserId, userId)
                .eq(Answer::getStatus, 1)
                .orderByDesc(Answer::getCreatedAt);

        Page<Answer> result = answerMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, size);
    }
}
