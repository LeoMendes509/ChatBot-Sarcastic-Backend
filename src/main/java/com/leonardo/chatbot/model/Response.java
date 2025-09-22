package com.leonardo.chatbot.model;

import java.time.LocalDateTime;

public class Response {

    private String text;
    private LocalDateTime dateTime;

    public Response() {}

    public Response(String responseText) {
        this.text = responseText;
        this.dateTime = LocalDateTime.now();
    }

    public String getText() { return text; }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

}

