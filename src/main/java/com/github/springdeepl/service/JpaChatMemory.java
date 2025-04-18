package com.github.springdeepl.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.domain.Limit;

import com.github.springdeepl.entity.ChatHistory;
import com.github.springdeepl.repository.ChatHistoryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaChatMemory implements ChatMemory {

    private final ChatHistoryRepository chatHistoryRepository;

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<ChatHistory> historyList = messages.stream()
            .map(msg -> new ChatHistory(null, conversationId, msg.getText(), msg.getMessageType(), LocalDateTime.now()))
            .collect(Collectors.toList());
        chatHistoryRepository.saveAll(historyList);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        return chatHistoryRepository.findBySessionIdOrderByCreateTimeDesc(conversationId, Limit.of(lastN)).stream()
            .map(msg -> new UserMessage(msg.getContent())).collect(Collectors.toList());
    }

    @Override
    public void clear(String conversationId) {
        chatHistoryRepository.deleteBySessionId(conversationId);
    }

}
