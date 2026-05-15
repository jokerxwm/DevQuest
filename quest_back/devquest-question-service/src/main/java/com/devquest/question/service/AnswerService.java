package com.devquest.question.service;

import com.devquest.common.dto.AnswerDTO;
import com.devquest.common.result.PageResult;
import com.devquest.question.entity.Answer;

public interface AnswerService {

    Answer createAnswer(Long userId, AnswerDTO dto);

    Answer getAnswerById(Long answerId);

    PageResult<Answer> getAnswersByQuestion(Long questionId, int page, int size, String sort);

    void updateAnswer(Long userId, Long answerId, String content);

    void deleteAnswer(Long userId, Long answerId);

    void acceptAnswer(Long userId, Long questionId, Long answerId);

    PageResult<Answer> getUserAnswers(Long userId, int page, int size);
}
