package com.github.springaix.chat;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.springaix.entity.ChatHistory;
import com.github.springaix.repository.ChatHistoryRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class ChatHistoryController {

    private final ChatHistoryRepository chatHistoryRepository;

    @GetMapping("/list")
    public List<ChatHistory> listHistory() {
        return chatHistoryRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
    }

    @GetMapping("/clear")
    public void clearHistory(String conversationId) {
        chatHistoryRepository.deleteBySessionId(conversationId);
    }

}
