package com.leonardo.chatbot.controller;

import com.leonardo.chatbot.model.Question;
import com.leonardo.chatbot.model.Response;
import com.leonardo.chatbot.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    // Serviço que gera respostas do chatbot
    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    /**
     * Endpoint que recebe uma pergunta e retorna a resposta do chatbot
     * @param question Objeto Question contendo o texto da pergunta
     * @return Response Objeto com a resposta do chatbot
     */
    @PostMapping("/answer")
    public Response answer(@RequestBody Question question) {
        // Gera resposta do chatbot com base na pergunta recebida
        return chatbotService.generateResponse(question);
    }
}
