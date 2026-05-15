package com.devquest.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.ResultCode;
import com.devquest.question.entity.Favorite;
import com.devquest.question.entity.Question;
import com.devquest.question.mapper.FavoriteMapper;
import com.devquest.question.mapper.QuestionMapper;
import com.devquest.question.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final QuestionMapper questionMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long questionId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getQuestionId, questionId);
        if (favoriteMapper.selectCount(queryWrapper) > 0) {
            throw new BizException(ResultCode.ALREADY_FAVORITED);
        }

        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BizException(ResultCode.QUESTION_NOT_FOUND);
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setQuestionId(questionId);
        favoriteMapper.insert(favorite);

        LambdaUpdateWrapper<Question> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Question::getId, questionId)
                .setSql("favorite_count = favorite_count + 1");
        questionMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long questionId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getQuestionId, questionId);
        int deleted = favoriteMapper.delete(queryWrapper);

        if (deleted > 0) {
            LambdaUpdateWrapper<Question> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Question::getId, questionId)
                    .setSql("favorite_count = favorite_count - 1");
            questionMapper.update(null, updateWrapper);
        }
    }

    @Override
    public boolean isFavorited(Long userId, Long questionId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getQuestionId, questionId);
        return favoriteMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public PageResult<Question> getUserFavorites(Long userId, int page, int size) {
        Page<Favorite> favoritePage = new Page<>(page, size);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreatedAt);

        Page<Favorite> favorites = favoriteMapper.selectPage(favoritePage, wrapper);
        List<Long> questionIds = favorites.getRecords().stream()
                .map(Favorite::getQuestionId)
                .collect(Collectors.toList());

        if (questionIds.isEmpty()) {
            return PageResult.of(List.of(), 0, page, size);
        }

        List<Question> questions = questionMapper.selectBatchIds(questionIds);
        return PageResult.of(questions, favorites.getTotal(), page, size);
    }
}
