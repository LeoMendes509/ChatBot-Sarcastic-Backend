package com.leonardo.chatbot.repository;

import com.leonardo.chatbot.model.ChatHistory;
import com.leonardo.chatbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory , Long> {

    List<ChatHistory> findByUser(User user);

    List<ChatHistory> findByUserAndSessionNameAndTimestampAfter(User user, String sessionName, LocalDateTime timestamp);

    List<ChatHistory> findByUserAndTimestampAfter(User user, LocalDateTime cutoff);

    void deleteByUserAndSessionName(User user, String sessionName);

    void deleteByUser(User user);
}
