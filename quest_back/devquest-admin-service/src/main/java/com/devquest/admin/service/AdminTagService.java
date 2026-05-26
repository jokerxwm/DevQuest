package com.devquest.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.devquest.admin.entity.Tag;
import com.devquest.admin.mapper.TagMapper;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminTagService {

    private final TagMapper tagMapper;

    public PageResult<Tag> getTags(int page, int size, String keyword) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Tag::getName, keyword)
                    .or()
                    .like(Tag::getDescription, keyword);
        }
        wrapper.orderByDesc(Tag::getCreatedAt);
        Page<Tag> result = tagMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, size);
    }

    public Tag createTag(Tag tag) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, tag.getName());
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new BizException(2001, "标签名已存在");
        }
        tag.setQuestionCount(0);
        tagMapper.insert(tag);
        return tag;
    }

    public Tag updateTag(Long id, Tag tag) {
        Tag existing = tagMapper.selectById(id);
        if (existing == null) {
            throw new BizException(ResultCode.TAG_NOT_FOUND);
        }
        existing.setName(tag.getName());
        existing.setDescription(tag.getDescription());
        existing.setColor(tag.getColor());
        tagMapper.updateById(existing);
        return existing;
    }

    public void deleteTag(Long id) {
        Tag existing = tagMapper.selectById(id);
        if (existing == null) {
            throw new BizException(ResultCode.TAG_NOT_FOUND);
        }
        tagMapper.deleteById(id);
    }
}
