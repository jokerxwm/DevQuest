package com.devquest.ai.service;

import com.devquest.ai.entity.AiConversation;
import com.devquest.ai.entity.AiMessage;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AiService {

    AiConversation createConversation(Long userId, String title);

    List<AiConversation> getUserConversations(Long userId);

    AiConversation getConversation(Long conversationId);

    void deleteConversation(Long userId, Long conversationId);

    List<AiMessage> getConversationMessages(Long conversationId);

    Flux<String> chat(Long userId, Long conversationId, String message);

    String generateAnswer(String question);

    String auditContent(String content, String contentType);
}
