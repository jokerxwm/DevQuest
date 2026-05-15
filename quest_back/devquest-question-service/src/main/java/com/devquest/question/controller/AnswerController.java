package com.devquest.question.controller;

import com.devquest.common.dto.AnswerDTO;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.R;
import com.devquest.common.util.UserContext;
import com.devquest.question.entity.Answer;
import com.devquest.question.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public R<Answer> createAnswer(@Valid @RequestBody AnswerDTO dto) {
        Long userId = UserContext.getRequiredUserId();
        return R.ok(answerService.createAnswer(userId, dto));
    }

    @GetMapping("/{answerId}")
    public R<Answer> getAnswer(@PathVariable Long answerId) {
        return R.ok(answerService.getAnswerById(answerId));
    }

    @GetMapping("/question/{questionId}")
    public R<PageResult<Answer>> getAnswersByQuestion(
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String sort) {
        return R.ok(answerService.getAnswersByQuestion(questionId, page, size, sort));
    }

    @PutMapping("/{answerId}")
    public R<Void> updateAnswer(@PathVariable Long answerId, @RequestBody String content) {
        Long userId = UserContext.getRequiredUserId();
        answerService.updateAnswer(userId, answerId, content);
        return R.ok();
    }

    @DeleteMapping("/{answerId}")
    public R<Void> deleteAnswer(@PathVariable Long answerId) {
        Long userId = UserContext.getRequiredUserId();
        answerService.deleteAnswer(userId, answerId);
        return R.ok();
    }

    @PostMapping("/{answerId}/accept/{questionId}")
    public R<Void> acceptAnswer(@PathVariable Long questionId, @PathVariable Long answerId) {
        Long userId = UserContext.getRequiredUserId();
        answerService.acceptAnswer(userId, questionId, answerId);
        return R.ok();
    }

    @GetMapping("/user/{userId}")
    public R<PageResult<Answer>> getUserAnswers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(answerService.getUserAnswers(userId, page, size));
    }
}
