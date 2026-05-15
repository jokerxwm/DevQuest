package com.devquest.question.controller;

import com.devquest.common.result.R;
import com.devquest.question.entity.Tag;
import com.devquest.question.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public R<List<Tag>> getAllTags() {
        return R.ok(tagService.getAllTags());
    }

    @GetMapping("/hot")
    public R<List<Tag>> getHotTags(@RequestParam(defaultValue = "10") int limit) {
        return R.ok(tagService.getHotTags(limit));
    }

    @GetMapping("/{tagId}")
    public R<Tag> getTag(@PathVariable Long tagId) {
        return R.ok(tagService.getTagById(tagId));
    }

    @GetMapping("/name/{name}")
    public R<Tag> getTagByName(@PathVariable String name) {
        return R.ok(tagService.getTagByName(name));
    }

    @GetMapping("/question/{questionId}")
    public R<List<Tag>> getTagsByQuestion(@PathVariable Long questionId) {
        return R.ok(tagService.getTagsByQuestion(questionId));
    }
}
