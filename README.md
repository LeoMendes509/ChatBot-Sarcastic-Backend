# ChatBot Sarcastic Backend

## Overview / Visão Geral

**English:**  
Backend built with Spring Boot 3.5.3 and Java 17, using MySQL as database. JWT authentication is implemented for secure access. All endpoints have been tested in Postman.

**Português:**  
Backend construído com Spring Boot 3.5.3 e Java 17, utilizando MySQL como banco de dados. Autenticação JWT foi implementada para acesso seguro. Todos os endpoints foram testados no Postman.

---

## Features / Funcionalidades

### Authentication / Autenticação (`/api/auth`)

- `POST /register` – Register a new user / Registrar um novo usuário.  
- `POST /login` – Authenticate and receive JWT token / Autenticar e receber token JWT.  
- `GET /me` – Retrieve current user information (authentication required) / Recuperar informações do usuário atual (requer autenticação).  
- `DELETE /delete` – Delete user account (authentication required) / Deletar conta do usuário (requer autenticação).  

### Chat (`/api/chat`)

- `POST /send` – Send message to chatbot and receive response / Enviar mensagem para o chatbot e receber resposta.  
- `GET /history` – Retrieve chat history for a specific session (authentication required) / Recuperar histórico de uma sessão específica (requer autenticação).  
- `DELETE /session` – Delete a specific chat session / Deletar uma sessão de chat específica.  
- `DELETE /sessions` – Delete all chat sessions of the authenticated user / Deletar todas as sessões de chat do usuário autenticado.  

### Security / Segurança

- JWT-based stateless authentication / Autenticação JWT sem estado (stateless).  
- BCrypt password encoding / Criptografia de senha com BCrypt.  
- CSRF disabled for API/Postman access / CSRF desativado para uso via API/Postman.  
- All endpoints except `/register` and `/login` require authentication / Todos os endpoints, exceto `/register` e `/login`, exigem autenticação.  

---

## Documentation / Documentação

- Swagger / OpenAPI available at: `/swagger-ui.html` or `/swagger-ui/index.html` depending on Springdoc configuration / Swagger UI disponível em `/swagger-ui.html` ou `/swagger-ui/index.html`.  
- API documentation reflects all controllers and endpoints / A documentação da API reflete todos os controllers e endpoints.  

---

## Configuration / Configuração

- `OpenAIConfig` – Handles OpenAI API key, URL, model, and temperature / Gerencia chave, URL, modelo e temperatura da API OpenAI.  
- `SwaggerConfig` – Customizes API documentation metadata / Personaliza informações da documentação da API.  
- `SecurityConfig` and `JwtAuthFilter` – Security filters and JWT authentication / Filtros de segurança e autenticação JWT.  

---

## Next Steps / Próximos Passos

- Frontend integration with React / Integração com front-end em React.  
- Optional: add detailed Swagger descriptions / Opcional: adicionar descrições detalhadas no Swagger.  
- Deployment setup (Docker or cloud) / Configuração para deployment (Docker ou nuvem).  

---

## Project Details / Detalhes do Projeto

- **Java version / Versão do Java:** 17  
- **Spring Boot version / Versão do Spring Boot:** 3.5.3  
- **Database / Banco de Dados:** MySQL  
- **Port / Porta:** 8081  

