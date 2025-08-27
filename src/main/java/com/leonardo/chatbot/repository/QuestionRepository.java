package com.leonardo.chatbot.repository;

import com.leonardo.chatbot.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question , Long> {
    List<Question> findByUserId(Long userId);
}
