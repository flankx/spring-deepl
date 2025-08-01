package com.github.springaix.repository;

import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.springaix.entity.ChatHistory;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    List<ChatHistory> findBySessionId(String sessionId);

    List<ChatHistory> findBySessionIdOrderByCreateTimeDesc(String sessionId, Limit limit);

    @Modifying
    // @Query("DELETE FROM ChatHistory WHERE sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") String sessionId);

}
