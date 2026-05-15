package com.devquest.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.ResultCode;
import com.devquest.question.entity.QuestionTag;
import com.devquest.question.entity.Tag;
import com.devquest.question.mapper.QuestionTagMapper;
import com.devquest.question.mapper.TagMapper;
import com.devquest.question.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final QuestionTagMapper questionTagMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String HOT_TAGS_KEY = "tag:hot";

    @Override
    public List<Tag> getAllTags() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Tag::getQuestionCount);
        return tagMapper.selectList(wrapper);
    }

    @Override
    public List<Tag> getHotTags(int limit) {
        Object cached = redisTemplate.opsForValue().get(HOT_TAGS_KEY);
        if (cached instanceof List) {
            return (List<Tag>) cached;
        }

        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Tag::getQuestionCount)
                .last("LIMIT " + limit);
        List<Tag> tags = tagMapper.selectList(wrapper);

        redisTemplate.opsForValue().set(HOT_TAGS_KEY, tags, 3600, TimeUnit.SECONDS);
        return tags;
    }

    @Override
    public Tag getTagById(Long tagId) {
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BizException("标签不存在");
        }
        return tag;
    }

    @Override
    public Tag getTagByName(String name) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, name);
        return tagMapper.selectOne(wrapper);
    }

    @Override
    public List<Tag> getTagsByQuestion(Long questionId) {
        LambdaQueryWrapper<QuestionTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionTag::getQuestionId, questionId);
        List<QuestionTag> questionTags = questionTagMapper.selectList(wrapper);

        List<Long> tagIds = questionTags.stream()
                .map(QuestionTag::getTagId)
                .collect(Collectors.toList());

        if (tagIds.isEmpty()) {
            return List.of();
        }

        return tagMapper.selectBatchIds(tagIds);
    }
}
