package com.devquest.question.service;

import com.devquest.common.result.PageResult;
import com.devquest.question.entity.Draft;

public interface DraftService {

    Draft saveDraft(Long userId, Draft draft);

    Draft updateDraft(Long userId, Long draftId, Draft draft);

    void deleteDraft(Long userId, Long draftId);

    Draft getDraft(Long userId, Long draftId);

    PageResult<Draft> getDrafts(Long userId, String type, int page, int size);
}
