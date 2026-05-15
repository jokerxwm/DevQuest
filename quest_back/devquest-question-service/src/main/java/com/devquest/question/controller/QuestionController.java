package com.devquest.question.controller;

import com.devquest.common.dto.QuestionDTO;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.R;
import com.devquest.common.util.UserContext;
import com.devquest.question.entity.Question;
import com.devquest.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public R<Question> createQuestion(@Valid @RequestBody QuestionDTO dto) {
        Long userId = UserContext.getRequiredUserId();
        return R.ok(questionService.createQuestion(userId, dto));
    }

    @GetMapping("/{questionId}")
    public R<Question> getQuestion(@PathVariable Long questionId) {
        questionService.incrementViewCount(questionId);
        return R.ok(questionService.getQuestionDetail(questionId));
    }

    @GetMapping("/list")
    public R<PageResult<Question>> getQuestionList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "newest") String sort) {
        return R.ok(questionService.getQuestionList(page, size, tagId, sort));
    }

    @GetMapping("/hot")
    public R<PageResult<Question>> getHotQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(questionService.getHotQuestions(page, size));
    }

    @GetMapping("/user/{userId}")
    public R<PageResult<Question>> getUserQuestions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(questionService.getUserQuestions(userId, page, size));
    }

    @PutMapping("/{questionId}")
    public R<Void> updateQuestion(@PathVariable Long questionId, @Valid @RequestBody QuestionDTO dto) {
        Long userId = UserContext.getRequiredUserId();
        questionService.updateQuestion(userId, questionId, dto);
        return R.ok();
    }

    @DeleteMapping("/{questionId}")
    public R<Void> deleteQuestion(@PathVariable Long questionId) {
        Long userId = UserContext.getRequiredUserId();
        questionService.deleteQuestion(userId, questionId);
        return R.ok();
    }
}
