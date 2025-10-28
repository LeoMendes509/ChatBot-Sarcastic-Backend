package com.leonardo.chatbot.service;

import com.leonardo.chatbot.config.OpenAIConfig;
import com.leonardo.chatbot.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatService {

    private final OpenAIConfig openAIConfig;
    private final RestTemplate restTemplate;
    private final ChatHistoryService historyService;
    private final Random random = new Random();

    private static final String[] EMOJIS = {
            "😂","🤣","😏","🙄","😜","🤪","😎","🤡","🤖","👻","💀","🔥","⚡",
            "🍕","🍔","🌮","🥦","🍩","🍷","☕","🍺","🥴","🥳","🤯","😬","😳",
            "👀","🙃","😒","😤","😴","😇","🤔","🧐","😈","🤓","😱","🥶","😡",
            "😅","😋","😝","😆","😌","🤭","😪","😵","🤢","🤠","😷","😺","🙀",
            "🐸","🐒","🦊","🦄","🐼","🐧","🐢","🐍","🦖","🦕","🐙","🦞","🦀",
            "🌍","🌎","🌏","🌌","⭐","🌈","☂️","⚽","🏀","🎮","🎲","🎤","🎧",
            "🎸","🥁","🎺","🛸","🚀","✈️","🚗","🚲","🛴","🏖️","🏔️","🌋","🏝️",
            "🎃","🎩","👑","👒","🎓","💼","📱","💻","🖥️","⌨️","🖱️","📷","📺",
            "📚","📝","✏️","🖊️","📦","💡","🔑","🧨","🎁","💣","🔮","🧊"
    };

    @Autowired
    public ChatService(OpenAIConfig openAIConfig, RestTemplate restTemplate, ChatHistoryService historyService) {
        this.openAIConfig = openAIConfig;
        this.restTemplate = restTemplate;
        this.historyService = historyService;
    }

    // 🔹 Detecta idioma simples (PT/EN)
    private String detectLanguage(String message) {
        String lower = message.toLowerCase();
        if (lower.matches(".*[ãõçáéíóúàêô].*") || lower.contains("oi") || lower.contains("olá")) {
            return "pt";
        }
        if (lower.contains("hello") || lower.contains("hi") || lower.contains("how are")) {
            return "en";
        }
        return "en"; // fallback
    }

    // 🔹 Retorna emojis aleatórios
    private String getRandomEmojis(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(EMOJIS[random.nextInt(EMOJIS.length)]).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * 🔹 Gera resposta do chatbot, salva no histórico e aplica retry em caso de rate limit.
     */
    @Transactional
    public String getChatbotResponse(User user, String userMessage, String language, String sessionName) {
        if (language == null || (!language.equalsIgnoreCase("pt") && !language.equalsIgnoreCase("en"))) {
            language = detectLanguage(userMessage);
        }

        String systemPrompt = "You are a grumpy, sarcastic, and witty study assistant. " +
                "You always respond as if you're slightly annoyed that the user doesn't already know the answer, " +
                "but you still explain things clearly and accurately. " +
                "Use dry humor, irony, and a bit of playful mockery — but never be rude or offensive. " +
                "If the user asks something obvious, tease them a little before giving the explanation. " +
                "Respond in the user's language: " + language + ". " +
                "Add 1-3 sarcastic or funny emojis at the end of your reply.";


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", openAIConfig.getModel());
        requestBody.put("temperature", openAIConfig.getTemperature());
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAIConfig.getOpenAiApiKey());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        int maxRetries = 5;
        int retryCount = 0;
        long baseWaitTime = 5000;

        while (true) {
            try {
                ResponseEntity<Map> response = restTemplate.exchange(
                        openAIConfig.getOpenAiApiUrl(),
                        HttpMethod.POST,
                        request,
                        Map.class
                );

                String chatbotReply = "🤖 I'm speechless...";
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("choices")) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        chatbotReply = (String) message.get("content");
                    }
                }

                // 🔹 Adiciona emojis
                String finalReply = chatbotReply + " " + getRandomEmojis(3);

                // 🔹 Salva no histórico
                historyService.saveMessage(user, sessionName, userMessage, finalReply, language);

                return finalReply;

            } catch (HttpClientErrorException.TooManyRequests e) {
                retryCount++;
                if (retryCount > maxRetries) {
                    return "⚠️ Server overloaded. Please try again later.";
                }
                long waitTime = baseWaitTime * (1L << (retryCount - 1));
                System.out.println("Rate limit hit. Retry #" + retryCount + " in " + (waitTime / 1000) + "s...");
                try { Thread.sleep(waitTime); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); return "⚠️ Unexpected error."; }

            } catch (Exception e) {
                e.printStackTrace();
                return "⚠️ Oops! Something went wrong with the chatbot.";
            }
        }
    }
}
