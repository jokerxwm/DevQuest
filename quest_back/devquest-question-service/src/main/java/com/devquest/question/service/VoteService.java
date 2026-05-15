package com.devquest.question.service;

public interface VoteService {

    void vote(Long userId, Long targetId, int targetType, int voteType);

    void cancelVote(Long userId, Long targetId, int targetType);

    Integer getUserVote(Long userId, Long targetId, int targetType);
}
