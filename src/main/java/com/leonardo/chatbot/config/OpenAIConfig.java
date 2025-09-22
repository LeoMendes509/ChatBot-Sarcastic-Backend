package com.leonardo.chatbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Value("${openai.model:gpt-4o-mini}") // modelo padrão
    private String model;

    @Value("${openai.temperature:0.7}") // temperatura padrão
    private double temperature;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getOpenAiApiKey() {
        return openAiApiKey;
    }

    public String getOpenAiApiUrl() {
        return openAiApiUrl;
    }

    public String getModel() {
        return model;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getApiKey() {
        return openAiApiKey;
    }

    public String getApiUrl() {
        return openAiApiUrl;
    }



}
