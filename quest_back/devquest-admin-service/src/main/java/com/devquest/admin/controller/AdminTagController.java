package com.devquest.admin.controller;

import com.devquest.admin.entity.Tag;
import com.devquest.admin.service.AdminTagService;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final AdminTagService adminTagService;

    @GetMapping
    public R<PageResult<Tag>> getTags(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        return R.ok(adminTagService.getTags(page, size, keyword));
    }

    @PostMapping
    public R<Tag> createTag(@RequestBody Tag tag) {
        return R.ok(adminTagService.createTag(tag));
    }

    @PutMapping("/{id}")
    public R<Tag> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        return R.ok(adminTagService.updateTag(id, tag));
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteTag(@PathVariable Long id) {
        adminTagService.deleteTag(id);
        return R.ok();
    }
}
