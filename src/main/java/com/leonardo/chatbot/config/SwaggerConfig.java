package com.leonardo.chatbot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chatbot Sarcastic API")
                        .version("1.0")
                        .description("Documentation of backend endpoints for the sarcastic chatbot project.")
                        .contact(new Contact()
                                .name("Leonardo Mendes")
                                .email("lmrodrigues.dev509@gmail.com")
                                .url("https://github.com/LeoMendes509"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org"))
                )
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local Dev"),
                        new Server().url("https://chatbot-sarcastic.com/api").description("Production")
                ));
    }
}
