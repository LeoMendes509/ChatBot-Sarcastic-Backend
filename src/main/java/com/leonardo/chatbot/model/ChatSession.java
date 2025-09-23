package com.leonardo.chatbot.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_sessions")
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String newChat;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    //Getters e setters


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNewChat() {
        return newChat;
    }

    public void setNewChat(String newChat) {
        this.newChat = newChat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
