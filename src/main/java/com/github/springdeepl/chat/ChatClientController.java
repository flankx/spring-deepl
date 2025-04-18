package com.github.springdeepl.chat;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class ChatClientController {

    private final ChatClient chatClient;

    @GetMapping(value = "/generate")
    public String generate(@RequestParam String conversationId,
        @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        final String sessionId = StringUtils.isNotBlank(conversationId) ? conversationId : UUID.randomUUID().toString();
        return this.chatClient.prompt(message).advisors(advisorSpec -> advisorSpec
            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)).call()
            .content();
    }

    @GetMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String conversationId,
        @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        final String sessionId = StringUtils.isNotBlank(conversationId) ? conversationId : UUID.randomUUID().toString();
        return this.chatClient.prompt(message).advisors(advisorSpec -> advisorSpec
            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)).stream()
            .content();
    }

}
