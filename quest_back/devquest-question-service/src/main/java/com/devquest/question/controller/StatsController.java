package com.devquest.question.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.devquest.common.result.R;
import com.devquest.question.entity.Answer;
import com.devquest.question.entity.Question;
import com.devquest.question.mapper.AnswerMapper;
import com.devquest.question.mapper.QuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;

    @GetMapping
    public R<Map<String, Object>> getCommunityStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQuestions", questionMapper.selectCount(null));
        stats.put("totalAnswers", answerMapper.selectCount(null));
        stats.put("totalUsers", questionMapper.selectCount(
                new LambdaQueryWrapper<Question>().select(Question::getUserId).groupBy(Question::getUserId)
        ));
        return R.ok(stats);
    }
}
