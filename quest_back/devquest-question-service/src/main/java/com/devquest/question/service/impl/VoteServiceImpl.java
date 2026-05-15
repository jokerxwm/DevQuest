package com.devquest.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.ResultCode;
import com.devquest.question.entity.Answer;
import com.devquest.question.entity.Question;
import com.devquest.question.entity.Vote;
import com.devquest.question.mapper.AnswerMapper;
import com.devquest.question.mapper.QuestionMapper;
import com.devquest.question.mapper.VoteMapper;
import com.devquest.question.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteMapper voteMapper;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void vote(Long userId, Long targetId, int targetType, int voteType) {
        LambdaQueryWrapper<Vote> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Vote::getUserId, userId)
                .eq(Vote::getTargetId, targetId)
                .eq(Vote::getTargetType, targetType);
        Vote existingVote = voteMapper.selectOne(queryWrapper);

        if (existingVote != null) {
            if (existingVote.getVoteType() == voteType) {
                throw new BizException(ResultCode.ALREADY_VOTED);
            }

            int diff = voteType - existingVote.getVoteType();
            existingVote.setVoteType(voteType);
            voteMapper.updateById(existingVote);
            updateVoteCount(targetId, targetType, diff);
        } else {
            Vote vote = new Vote();
            vote.setUserId(userId);
            vote.setTargetId(targetId);
            vote.setTargetType(targetType);
            vote.setVoteType(voteType);
            voteMapper.insert(vote);
            updateVoteCount(targetId, targetType, voteType);
        }
    }

    @Override
    @Transactional
    public void cancelVote(Long userId, Long targetId, int targetType) {
        LambdaQueryWrapper<Vote> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Vote::getUserId, userId)
                .eq(Vote::getTargetId, targetId)
                .eq(Vote::getTargetType, targetType);
        Vote vote = voteMapper.selectOne(queryWrapper);

        if (vote != null) {
            updateVoteCount(targetId, targetType, -vote.getVoteType());
            voteMapper.delete(queryWrapper);
        }
    }

    @Override
    public Integer getUserVote(Long userId, Long targetId, int targetType) {
        LambdaQueryWrapper<Vote> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Vote::getUserId, userId)
                .eq(Vote::getTargetId, targetId)
                .eq(Vote::getTargetType, targetType);
        Vote vote = voteMapper.selectOne(queryWrapper);
        return vote != null ? vote.getVoteType() : 0;
    }

    private void updateVoteCount(Long targetId, int targetType, int diff) {
        if (targetType == 1) {
            LambdaUpdateWrapper<Question> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Question::getId, targetId)
                    .setSql("vote_count = vote_count + " + diff);
            questionMapper.update(null, updateWrapper);
            redisTemplate.delete("question:detail:" + targetId);
        } else if (targetType == 2) {
            LambdaUpdateWrapper<Answer> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Answer::getId, targetId)
                    .setSql("vote_count = vote_count + " + diff);
            answerMapper.update(null, updateWrapper);
        }
    }
}
