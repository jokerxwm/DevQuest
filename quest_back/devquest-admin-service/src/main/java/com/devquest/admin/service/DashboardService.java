package com.devquest.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.devquest.admin.entity.Answer;
import com.devquest.admin.entity.Question;
import com.devquest.admin.entity.User;
import com.devquest.admin.mapper.AnswerMapper;
import com.devquest.admin.mapper.QuestionMapper;
import com.devquest.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserMapper userMapper;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalQuestions", questionMapper.selectCount(null));
        stats.put("totalAnswers", answerMapper.selectCount(null));

        LambdaQueryWrapper<User> todayUsers = new LambdaQueryWrapper<>();
        stats.put("todayUsers", userMapper.selectCount(todayUsers));

        LambdaQueryWrapper<Question> todayQuestions = new LambdaQueryWrapper<>();
        stats.put("todayQuestions", questionMapper.selectCount(todayQuestions));

        return stats;
    }
}
