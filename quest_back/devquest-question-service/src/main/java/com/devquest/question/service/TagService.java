package com.devquest.question.service;

import com.devquest.question.entity.Tag;

import java.util.List;

public interface TagService {

    List<Tag> getAllTags();

    List<Tag> getHotTags(int limit);

    Tag getTagById(Long tagId);

    Tag getTagByName(String name);

    List<Tag> getTagsByQuestion(Long questionId);
}
