package com.devquest.question.service;

import com.devquest.common.result.PageResult;
import com.devquest.question.entity.Comment;

import java.util.List;

public interface CommentService {

    Comment addComment(Long userId, Long targetId, int targetType, String content);

    void deleteComment(Long userId, Long commentId);

    List<Comment> getComments(Long targetId, int targetType);
}
