package com.devquest.question.controller;

import com.devquest.common.result.R;
import com.devquest.common.util.UserContext;
import com.devquest.question.entity.Comment;
import com.devquest.question.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public R<Comment> addComment(@RequestBody Map<String, Object> params) {
        Long userId = UserContext.getRequiredUserId();
        Long targetId = Long.valueOf(params.get("targetId").toString());
        int targetType = Integer.parseInt(params.get("targetType").toString());
        String content = params.get("content").toString();
        return R.ok(commentService.addComment(userId, targetId, targetType, content));
    }

    @DeleteMapping("/{commentId}")
    public R<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = UserContext.getRequiredUserId();
        commentService.deleteComment(userId, commentId);
        return R.ok();
    }

    @GetMapping
    public R<List<Comment>> getComments(
            @RequestParam Long targetId,
            @RequestParam int targetType) {
        return R.ok(commentService.getComments(targetId, targetType));
    }
}
