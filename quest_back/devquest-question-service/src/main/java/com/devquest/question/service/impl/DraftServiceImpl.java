package com.devquest.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.devquest.common.exception.BizException;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.ResultCode;
import com.devquest.question.entity.Draft;
import com.devquest.question.mapper.DraftMapper;
import com.devquest.question.service.DraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DraftServiceImpl implements DraftService {

    private final DraftMapper draftMapper;

    @Override
    public Draft saveDraft(Long userId, Draft draft) {
        draft.setUserId(userId);
        draftMapper.insert(draft);
        return draft;
    }

    @Override
    public Draft updateDraft(Long userId, Long draftId, Draft draft) {
        Draft existing = draftMapper.selectById(draftId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BizException(ResultCode.DRAFT_NOT_FOUND);
        }
        existing.setTitle(draft.getTitle());
        existing.setContent(draft.getContent());
        existing.setTags(draft.getTags());
        existing.setQuestionId(draft.getQuestionId());
        draftMapper.updateById(existing);
        return existing;
    }

    @Override
    public void deleteDraft(Long userId, Long draftId) {
        Draft existing = draftMapper.selectById(draftId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BizException(ResultCode.DRAFT_NOT_FOUND);
        }
        draftMapper.deleteById(draftId);
    }

    @Override
    public Draft getDraft(Long userId, Long draftId) {
        Draft draft = draftMapper.selectById(draftId);
        if (draft == null || !draft.getUserId().equals(userId)) {
            throw new BizException(ResultCode.DRAFT_NOT_FOUND);
        }
        return draft;
    }

    @Override
    public PageResult<Draft> getDrafts(Long userId, String type, int page, int size) {
        LambdaQueryWrapper<Draft> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Draft::getUserId, userId);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Draft::getType, type);
        }
        wrapper.orderByDesc(Draft::getUpdatedAt);
        Page<Draft> result = draftMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, size);
    }
}
