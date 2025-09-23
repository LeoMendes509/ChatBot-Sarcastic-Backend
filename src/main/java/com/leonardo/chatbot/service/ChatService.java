package com.leonardo.chatbot.service;

import com.leonardo.chatbot.config.OpenAIConfig;
import com.leonardo.chatbot.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

    // Detecta idioma simples (PT/EN) - fallback
    private String detectLanguage(String userMessage) {
        String lower = userMessage.toLowerCase();
        if (lower.matches(".*[ãõçáéíóúàêô].*") || lower.contains("oi") || lower.contains("olá")) {
            return "pt";
        }
        if (lower.contains("hello") || lower.contains("hi") || lower.contains("how are")) {
            return "en";
        }
        return "en";
    }

    private String getRandomEmojis(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int idx = random.nextInt(EMOJIS.length);
            sb.append(EMOJIS[idx]).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Retorna resposta do chatbot e salva no histórico
     * Implementa retry com backoff exponencial em caso de rate limit (429)
     */
    public String getChatbotResponse(User user, String userMessage, String language, String sessionName) {
        int maxRetries = 5;         // número máximo de tentativas
        int retryCount = 0;
        long baseWaitTime = 5000;   // tempo inicial de espera em ms (5s)

        if (language == null || (!language.equalsIgnoreCase("pt") && !language.equalsIgnoreCase("en"))) {
            language = detectLanguage(userMessage);
        }

        String systemPrompt = "You're a sarcastic, fun, and intelligent chatbot with a healthy sense of humor. " +
                "Always respond playfully and slightly mockingly, without being offensive. " +
                "Respond in the user's language: " + language + ". " +
                "Add 2-4 funny emojis at the end of your reply.";

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

                String finalReply = chatbotReply + " " + getRandomEmojis(3);
                historyService.saveMessage(user, sessionName, userMessage, finalReply, language);
                return finalReply;

            } catch (HttpClientErrorException.TooManyRequests e) {
                retryCount++;
                if (retryCount > maxRetries) {
                    return "⚠️ Server is overloaded. Please try again in a few seconds.";
                }
                long waitTime = baseWaitTime * (1L << (retryCount - 1)); // backoff exponencial
                System.out.println("API rate limit reached . Retry #" + retryCount + " in " + (waitTime / 1000) + "s...");
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return "⚠️ An unexpected error has occurred.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "⚠️ Oops! Something went wrong with the chatbot.";
            }
        }
    }
}
