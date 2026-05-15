package com.devquest.question.service;

import com.devquest.common.result.PageResult;
import com.devquest.question.entity.Question;

public interface FavoriteService {

    void addFavorite(Long userId, Long questionId);

    void removeFavorite(Long userId, Long questionId);

    boolean isFavorited(Long userId, Long questionId);

    PageResult<Question> getUserFavorites(Long userId, int page, int size);
}
