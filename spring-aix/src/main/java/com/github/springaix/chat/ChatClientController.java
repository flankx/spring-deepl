package com.github.springaix.chat;

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
        return this.chatClient.prompt(message)
            .advisors(advisorSpec -> advisorSpec
                .param("conversationId", sessionId)
                .param("chatMemoryRetrieveSize", 10)
            )
            .call()
            .content();
    }

    @GetMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String conversationId,
        @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        final String sessionId = StringUtils.isNotBlank(conversationId) ? conversationId : UUID.randomUUID().toString();
        return this.chatClient.prompt(message)
            .advisors(advisorSpec -> advisorSpec
                .param("conversationId", sessionId)
                .param("chatMemoryRetrieveSize", 10)
            )
            .stream()
            .content();
    }

}
