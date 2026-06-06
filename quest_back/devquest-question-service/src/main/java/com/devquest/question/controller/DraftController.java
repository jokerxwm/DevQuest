package com.devquest.question.controller;

import com.devquest.common.result.PageResult;
import com.devquest.common.result.R;
import com.devquest.common.util.UserContext;
import com.devquest.question.entity.Draft;
import com.devquest.question.service.DraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drafts")
@RequiredArgsConstructor
public class DraftController {

    private final DraftService draftService;

    @PostMapping
    public R<Draft> saveDraft(@RequestBody Draft draft) {
        Long userId = UserContext.getRequiredUserId();
        return R.ok(draftService.saveDraft(userId, draft));
    }

    @PutMapping("/{draftId}")
    public R<Draft> updateDraft(@PathVariable Long draftId, @RequestBody Draft draft) {
        Long userId = UserContext.getRequiredUserId();
        return R.ok(draftService.updateDraft(userId, draftId, draft));
    }

    @DeleteMapping("/{draftId}")
    public R<Void> deleteDraft(@PathVariable Long draftId) {
        Long userId = UserContext.getRequiredUserId();
        draftService.deleteDraft(userId, draftId);
        return R.ok();
    }

    @GetMapping("/{draftId}")
    public R<Draft> getDraft(@PathVariable Long draftId) {
        Long userId = UserContext.getRequiredUserId();
        return R.ok(draftService.getDraft(userId, draftId));
    }

    @GetMapping
    public R<PageResult<Draft>> getDrafts(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        Long userId = UserContext.getRequiredUserId();
        return R.ok(draftService.getDrafts(userId, type, page, size));
    }
}
