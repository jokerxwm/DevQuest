package com.devquest.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.devquest.ai.entity.AiConversation;
import com.devquest.ai.entity.AiMessage;
import com.devquest.ai.mapper.AiConversationMapper;
import com.devquest.ai.mapper.AiMessageMapper;
import com.devquest.ai.service.AiService;
import com.devquest.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final AiConversationMapper conversationMapper;
    private final AiMessageMapper messageMapper;

    @Override
    @Transactional
    public AiConversation createConversation(Long userId, String title) {
        AiConversation conversation = new AiConversation();
        conversation.setUserId(userId);
        conversation.setTitle(title != null ? title : "新对话");
        conversation.setStatus(1);
        conversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    public List<AiConversation> getUserConversations(Long userId) {
        LambdaQueryWrapper<AiConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiConversation::getUserId, userId)
                .eq(AiConversation::getStatus, 1)
                .orderByDesc(AiConversation::getUpdatedAt);
        return conversationMapper.selectList(wrapper);
    }

    @Override
    public AiConversation getConversation(Long conversationId) {
        AiConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || conversation.getStatus() == 0) {
            throw new BizException("会话不存在");
        }
        return conversation;
    }

    @Override
    @Transactional
    public void deleteConversation(Long userId, Long conversationId) {
        AiConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            throw new BizException("无权删除此会话");
        }
        conversation.setStatus(0);
        conversationMapper.updateById(conversation);
    }

    @Override
    public List<AiMessage> getConversationMessages(Long conversationId) {
        LambdaQueryWrapper<AiMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiMessage::getConversationId, conversationId)
                .orderByAsc(AiMessage::getCreatedAt);
        return messageMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public Flux<String> chat(Long userId, Long conversationId, String message) {
        AiConversation conversation = getConversation(conversationId);
        if (!conversation.getUserId().equals(userId)) {
            throw new BizException("无权访问此会话");
        }

        AiMessage userMessage = new AiMessage();
        userMessage.setConversationId(conversationId);
        userMessage.setRole("USER");
        userMessage.setContent(message);
        userMessage.setTokenCount(0);
        messageMapper.insert(userMessage);

        return Flux.create(sink -> {
            try {
                String response = generateAnswer(message);
                String[] words = response.split("(?<=\\G.{2})");
                for (String word : words) {
                    sink.next(word);
                    Thread.sleep(50);
                }
                sink.complete();

                AiMessage aiMessage = new AiMessage();
                aiMessage.setConversationId(conversationId);
                aiMessage.setRole("ASSISTANT");
                aiMessage.setContent(response);
                aiMessage.setTokenCount(0);
                messageMapper.insert(aiMessage);
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    @Override
    public String generateAnswer(String question) {
        return "这是一个AI生成的回答示例。您的问题是：" + question + "\n\n" +
                "在实际项目中，这里应该调用真正的AI服务（如OpenAI API、百度文心一言等）来生成回答。";
    }

    @Override
    public String auditContent(String content, String contentType) {
        log.info("审核内容: type={}, contentLength={}", contentType, content.length());
        return "{\"passed\": true, \"reason\": \"内容审核通过\"}";
    }
}
