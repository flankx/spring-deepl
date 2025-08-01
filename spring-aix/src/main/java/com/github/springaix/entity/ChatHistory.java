package com.github.springaix.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "session_id", length = 36)
    private String sessionId;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    private MessageType type;
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    @Override
    public String toString() {
        return "ChatHistory{" + "id=" + id + ", sessionId='" + sessionId + ", createTime=" + createTime + '}';
    }

}
