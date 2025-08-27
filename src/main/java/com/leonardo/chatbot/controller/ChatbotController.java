package com.leonardo.chatbot.controller;

import com.leonardo.chatbot.model.Question;

import com.leonardo.chatbot.model.Response;
import com.leonardo.chatbot.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/answer")
    public Response answer(@RequestBody Question question) {
        return chatbotService.generateResponse(question);
    }
}
