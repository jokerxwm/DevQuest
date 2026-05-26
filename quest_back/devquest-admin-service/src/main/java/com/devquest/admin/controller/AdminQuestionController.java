package com.devquest.admin.controller;

import com.devquest.admin.entity.Question;
import com.devquest.admin.service.AdminQuestionService;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final AdminQuestionService adminQuestionService;

    @GetMapping
    public R<PageResult<Question>> getQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return R.ok(adminQuestionService.getQuestions(page, size, keyword, status));
    }

    @PutMapping("/{questionId}/status")
    public R<Void> updateQuestionStatus(@PathVariable Long questionId, @RequestBody Map<String, Integer> params) {
        adminQuestionService.updateQuestionStatus(questionId, params.get("status"));
        return R.ok();
    }

    @PutMapping("/{questionId}/top")
    public R<Void> toggleTop(@PathVariable Long questionId) {
        adminQuestionService.toggleTop(questionId);
        return R.ok();
    }

    @DeleteMapping("/{questionId}")
    public R<Void> deleteQuestion(@PathVariable Long questionId) {
        adminQuestionService.deleteQuestion(questionId);
        return R.ok();
    }
}
