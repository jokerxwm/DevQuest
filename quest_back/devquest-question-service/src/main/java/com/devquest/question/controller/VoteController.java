package com.devquest.question.controller;

import com.devquest.common.result.R;
import com.devquest.common.util.UserContext;
import com.devquest.question.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public R<Void> vote(@RequestBody Map<String, Object> params) {
        Long userId = UserContext.getRequiredUserId();
        Long targetId = Long.valueOf(params.get("targetId").toString());
        int targetType = Integer.parseInt(params.get("targetType").toString());
        int voteType = Integer.parseInt(params.get("voteType").toString());
        voteService.vote(userId, targetId, targetType, voteType);
        return R.ok();
    }

    @DeleteMapping
    public R<Void> cancelVote(@RequestParam Long targetId, @RequestParam int targetType) {
        Long userId = UserContext.getRequiredUserId();
        voteService.cancelVote(userId, targetId, targetType);
        return R.ok();
    }

    @GetMapping("/status")
    public R<Map<String, Object>> getVoteStatus(
            @RequestParam Long targetId,
            @RequestParam int targetType) {
        Long userId = UserContext.getUserId();
        int vote = userId != null ? voteService.getUserVote(userId, targetId, targetType) : 0;
        return R.ok(Map.of("vote", vote));
    }
}
