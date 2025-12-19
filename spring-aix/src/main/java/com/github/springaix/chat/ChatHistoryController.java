package com.github.springaix.chat;

import java.util.List;

import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class ChatHistoryController {

    private final JdbcChatMemoryRepository memoryRepository;

    @GetMapping("/getAllConversationIds")
    public List<String> listHistory() {
        return memoryRepository.findConversationIds();
    }

    @GetMapping("/query/{conversationId}")
    public List<Message> queryHistory(@PathVariable String conversationId) {
        return memoryRepository.findByConversationId(conversationId);
    }

    @GetMapping("/clear/{conversationId}")
    public void clearHistory(@PathVariable String conversationId) {
        memoryRepository.deleteByConversationId(conversationId);
    }

}
