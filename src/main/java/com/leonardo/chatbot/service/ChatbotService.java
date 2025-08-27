package com.leonardo.chatbot.service;

import com.leonardo.chatbot.model.Question;
import com.leonardo.chatbot.model.Response;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

    public Response generateResponse(Question question) {
        String questionText = question.getText().toLowerCase();

        String responseText;
        if (questionText.contains("hi")) {
            responseText = "Oh, hi again... Haven't you said that enough already?";
        } else if (questionText.contains("how are you")) {
            responseText = "Me? Always better than you, obviously.";
        } else if (questionText.contains("what is your name")) {
            responseText = "I'm the chatbot you don't deserve.";
        } else {
            responseText = "I didn't get that. Try speaking properly or just give up.";
        }

        return new Response(responseText);
    }
}
