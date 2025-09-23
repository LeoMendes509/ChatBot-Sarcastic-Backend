package com.leonardo.chatbot.dto;

import java.time.LocalDateTime;

public class ChatHistoryDTO {

    private String sessionName; // Nome da sessão/conversa
    private String message;     // Mensagem enviada pelo usuário
    private String response;    // Resposta do bot
    private String language;    // Idioma da mensagem (pt/en)
    private LocalDateTime timestamp; // Horário da mensagem

    // Construtor
    public ChatHistoryDTO(String sessionName, String message, String response, String language, LocalDateTime timestamp) {
        this.sessionName = sessionName;
        this.message = message;
        this.response = response;
        this.language = language;
        this.timestamp = timestamp;
    }

    // Getters e Setters


    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
