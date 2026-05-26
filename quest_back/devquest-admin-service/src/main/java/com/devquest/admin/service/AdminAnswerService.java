package com.devquest.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.devquest.admin.entity.Answer;
import com.devquest.admin.mapper.AnswerMapper;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAnswerService {

    private final AnswerMapper answerMapper;

    public PageResult<Answer> getAnswers(int page, int size, Integer status) {
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Answer::getStatus, status);
        }
        wrapper.orderByDesc(Answer::getCreatedAt);
        Page<Answer> result = answerMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, size);
    }

    public void updateAnswerStatus(Long answerId, Integer status) {
        Answer answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new BizException(ResultCode.ANSWER_NOT_FOUND);
        }
        LambdaUpdateWrapper<Answer> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Answer::getId, answerId)
                .set(Answer::getStatus, status);
        answerMapper.update(null, wrapper);
    }

    public void deleteAnswer(Long answerId) {
        answerMapper.deleteById(answerId);
    }
}
