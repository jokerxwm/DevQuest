package com.devquest.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.devquest.admin.entity.Question;
import com.devquest.admin.mapper.QuestionMapper;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminQuestionService {

    private final QuestionMapper questionMapper;

    public PageResult<Question> getQuestions(int page, int size, String keyword, Integer status) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Question::getTitle, keyword);
        }
        if (status != null) {
            wrapper.eq(Question::getStatus, status);
        }
        wrapper.orderByDesc(Question::getCreatedAt);
        Page<Question> result = questionMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, size);
    }

    public void updateQuestionStatus(Long questionId, Integer status) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BizException(ResultCode.QUESTION_NOT_FOUND);
        }
        LambdaUpdateWrapper<Question> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Question::getId, questionId)
                .set(Question::getStatus, status);
        questionMapper.update(null, wrapper);
    }

    public void toggleTop(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BizException(ResultCode.QUESTION_NOT_FOUND);
        }
        LambdaUpdateWrapper<Question> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Question::getId, questionId)
                .set(Question::getIsTop, question.getIsTop() == 1 ? 0 : 1);
        questionMapper.update(null, wrapper);
    }

    public void deleteQuestion(Long questionId) {
        questionMapper.deleteById(questionId);
    }
}
