package com.devquest.question.service;

import com.devquest.common.dto.QuestionDTO;
import com.devquest.common.result.PageResult;
import com.devquest.question.entity.Question;

import java.util.List;

public interface QuestionService {

    Question createQuestion(Long userId, QuestionDTO dto);

    Question getQuestionDetail(Long questionId);

    PageResult<Question> getQuestionList(int page, int size, Long tagId, String sort);

    PageResult<Question> getHotQuestions(int page, int size);

    PageResult<Question> getUserQuestions(Long userId, int page, int size);

    void updateQuestion(Long userId, Long questionId, QuestionDTO dto);

    void deleteQuestion(Long userId, Long questionId);

    void incrementViewCount(Long questionId);
}
