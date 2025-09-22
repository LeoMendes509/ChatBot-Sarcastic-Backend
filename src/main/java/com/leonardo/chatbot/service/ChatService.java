package com.leonardo.chatbot.service;

import com.leonardo.chatbot.config.OpenAIConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatService {

    private final OpenAIConfig openAIConfig;
    private final RestTemplate restTemplate;
    private final Random random = new Random();

    // Array com +100 emojis
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
    public ChatService(OpenAIConfig openAIConfig, RestTemplate restTemplate) {
        this.openAIConfig = openAIConfig;
        this.restTemplate = restTemplate;
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
        return "en"; // fallback
    }

    // Retorna emojis aleatórios
    private String getRandomEmojis(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int idx = random.nextInt(EMOJIS.length);
            sb.append(EMOJIS[idx]).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Retorna resposta do chatbot.
     * @param userMessage mensagem do usuário
     * @param language idioma escolhido pelo front (pt/en). Se null ou inválido, fallback automático
     */
    public String getChatbotResponse(String userMessage, String language) {
        try {
            // fallback para detecção automática
            if (language == null || (!language.equalsIgnoreCase("pt") && !language.equalsIgnoreCase("en"))) {
                language = detectLanguage(userMessage);
            }

            String systemPrompt = "You're a sarcastic, fun, and intelligent chatbot with a healthy sense of humor. " +
            "Always respond playfully and slightly mockingly, without being offensive." +
                    "Respond in the user's language: " + language + ". " +
                    "Add 2-4 funny emojis at the end of your reply.";

            // Corpo da requisição
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

            ResponseEntity<Map> response = restTemplate.exchange(
                    openAIConfig.getOpenAiApiUrl(),
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String chatbotReply = (String) message.get("content");

                    return chatbotReply + " " + getRandomEmojis(3);
                }
            }

            return "🤖 Estou sem palavras... literalmente!";
        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Oops! Algo deu errado com o chatbot.";
        }
    }
}
