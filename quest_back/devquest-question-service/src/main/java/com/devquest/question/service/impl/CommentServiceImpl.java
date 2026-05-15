package com.devquest.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.ResultCode;
import com.devquest.question.entity.Comment;
import com.devquest.question.mapper.CommentMapper;
import com.devquest.question.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public Comment addComment(Long userId, Long targetId, int targetType, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setTargetId(targetId);
        comment.setTargetType(targetType);
        comment.setContent(content);
        comment.setStatus(1);
        commentMapper.insert(comment);
        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BizException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }

        comment.setStatus(0);
        commentMapper.updateById(comment);
    }

    @Override
    public List<Comment> getComments(Long targetId, int targetType) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getTargetId, targetId)
                .eq(Comment::getTargetType, targetType)
                .eq(Comment::getStatus, 1)
                .orderByAsc(Comment::getCreatedAt);
        return commentMapper.selectList(wrapper);
    }
}
