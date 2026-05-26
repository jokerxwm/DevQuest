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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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

    @GetMapping("/daily")
    public R<Map<String, Object>> getDailyList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Map<String, Object>> list = new ArrayList<>();
        String[][] data = {
                {"Spring Boot 3.2 新特性解析", "Spring Boot 3.2 带来了许多性能改进和新特性，包括虚拟线程支持、HTTP 接口客户端等。这些改进使得微服务开发更加高效。", "Spring Boot,Java,微服务"},
                {"Vue 3.4 版本发布", "Vue 3.4 正式发布，带来了更好的 TypeScript 支持、更快的编译速度和新的 defineModel 语法糖。", "Vue.js,前端,TypeScript"},
                {"Redis 7.4 新功能介绍", "Redis 7.4 引入了新的数据结构和性能优化，包括改进的内存管理和更快的持久化机制。", "Redis,缓存,数据库"},
                {"AI 代码助手对比评测", "对市面上主流的 AI 代码助手进行了全面对比，包括 GitHub Copilot、Cursor、Trae 等。", "AI,开发工具,效率"},
                {"Kubernetes 1.30 发布", "Kubernetes 1.30 带来了多项安全增强和性能优化，包括改进的调度器和网络策略。", "Kubernetes,Docker,容器化"},
        };
        for (int i = 0; i < data.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", i + 1);
            item.put("date", LocalDate.now().minusDays(i).toString());
            item.put("title", data[i][0]);
            item.put("content", data[i][1]);
            item.put("tags", data[i][2].split(","));
            item.put("createdAt", LocalDate.now().minusDays(i).atStartOfDay().toString());
            list.add(item);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return R.ok(result);
    }
}
