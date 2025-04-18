package com.github.springdeepl.config;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.springdeepl.repository.ChatHistoryRepository;
import com.github.springdeepl.service.JpaChatMemory;

@Configuration
public class ChatConfig {

    @Bean
    public ChatMemory chatMemory(ChatHistoryRepository chatHistoryRepository) {
        return new JpaChatMemory(chatHistoryRepository);
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory, VectorStore vectorStore) {
        return ChatClient.builder(chatModel).defaultSystem("你是一个智能知识库助手，需结合检索结果生成答案")
            .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), new MessageChatMemoryAdvisor(chatMemory),
                new QuestionAnswerAdvisor(vectorStore)))
            .build();
    }

}
