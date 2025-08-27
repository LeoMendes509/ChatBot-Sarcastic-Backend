package com.leonardo.chatbot.model;

import java.time.LocalDateTime;

public class Response {

    private Long id;
    private String text;
    private LocalDateTime dateTime;

    private Question question;

    public Response() {}

    public Response(String text , LocalDateTime dateTime , Question question) {
        this.text = text;
        this.dateTime = dateTime;
        this.question = question;
    }

    public Response(String responseText) {
        this.text = responseText;
        this.dateTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
