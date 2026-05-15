package com.devquest.ai.controller;

import com.devquest.ai.entity.AiConversation;
import com.devquest.ai.entity.AiMessage;
import com.devquest.ai.service.AiService;
import com.devquest.common.result.R;
import com.devquest.common.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/conversations")
    public R<AiConversation> createConversation(@RequestBody(required = false) Map<String, String> params) {
        Long userId = UserContext.getRequiredUserId();
        String title = params != null ? params.get("title") : null;
        return R.ok(aiService.createConversation(userId, title));
    }

    @GetMapping("/conversations")
    public R<List<AiConversation>> getConversations() {
        Long userId = UserContext.getRequiredUserId();
        return R.ok(aiService.getUserConversations(userId));
    }

    @GetMapping("/conversations/{conversationId}")
    public R<AiConversation> getConversation(@PathVariable Long conversationId) {
        return R.ok(aiService.getConversation(conversationId));
    }

    @DeleteMapping("/conversations/{conversationId}")
    public R<Void> deleteConversation(@PathVariable Long conversationId) {
        Long userId = UserContext.getRequiredUserId();
        aiService.deleteConversation(userId, conversationId);
        return R.ok();
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public R<List<AiMessage>> getMessages(@PathVariable Long conversationId) {
        return R.ok(aiService.getConversationMessages(conversationId));
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody Map<String, Object> params) {
        Long userId = UserContext.getRequiredUserId();
        Long conversationId = Long.valueOf(params.get("conversationId").toString());
        String message = params.get("message").toString();
        return aiService.chat(userId, conversationId, message);
    }

    @PostMapping("/generate")
    public R<Map<String, String>> generateAnswer(@RequestBody Map<String, String> params) {
        String question = params.get("question");
        String answer = aiService.generateAnswer(question);
        return R.ok(Map.of("answer", answer));
    }

    @PostMapping("/audit")
    public R<Map<String, Object>> auditContent(@RequestBody Map<String, String> params) {
        String content = params.get("content");
        String contentType = params.get("contentType");
        String result = aiService.auditContent(content, contentType);
        return R.ok(Map.of("result", result));
    }
}
