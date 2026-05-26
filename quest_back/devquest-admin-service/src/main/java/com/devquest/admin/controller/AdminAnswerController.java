package com.devquest.admin.controller;

import com.devquest.admin.entity.Answer;
import com.devquest.admin.service.AdminAnswerService;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/answers")
@RequiredArgsConstructor
public class AdminAnswerController {

    private final AdminAnswerService adminAnswerService;

    @GetMapping
    public R<PageResult<Answer>> getAnswers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer status) {
        return R.ok(adminAnswerService.getAnswers(page, size, status));
    }

    @PutMapping("/{answerId}/status")
    public R<Void> updateAnswerStatus(@PathVariable Long answerId, @RequestBody Map<String, Integer> params) {
        adminAnswerService.updateAnswerStatus(answerId, params.get("status"));
        return R.ok();
    }

    @DeleteMapping("/{answerId}")
    public R<Void> deleteAnswer(@PathVariable Long answerId) {
        adminAnswerService.deleteAnswer(answerId);
        return R.ok();
    }
}
