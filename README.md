# ChatBot Sarcastic Backend

## 🌟 Overview / Visão Geral

**English:**  
This is a modern backend built with **Java 17**, **Spring Boot 3.5.3**, and **MySQL**, implementing **secure JWT authentication** and following best practices for API development. All endpoints have been fully tested.  

It powers the **ChatBot Sarcastic**, an intelligent and witty chatbot that answers your study questions like a real teacher with a touch of sarcasm. It is educational, interactive, and keeps learning fun and engaging.  

**Português:**  
Este é um backend moderno, construído com **Java 17**, **Spring Boot 3.5.3** e **MySQL**, implementando **autenticação JWT segura** e seguindo as melhores práticas para desenvolvimento de APIs. Todos os endpoints foram completamente testados.  

Ele alimenta o **ChatBot Sarcastic**, um chatbot inteligente e espirituoso que responde às suas dúvidas de estudo como um verdadeiro professor, mas com uma pitada de sarcasmo. É educativo, interativo e torna o aprendizado divertido e envolvente.  

---

## 🚀 Features / Funcionalidades

### Authentication / Autenticação (`/api/auth`)

- `POST /register` – Register a new user / Registrar um novo usuário.  
- `POST /login` – Authenticate and receive JWT token / Autenticar e receber token JWT.  
- `GET /me` – Retrieve current user information (authentication required) / Recuperar informações do usuário atual (requer autenticação).  
- `DELETE /delete` – Delete user account (authentication required) / Deletar conta do usuário (requer autenticação).  

### Chat / Chat (`/api/chat`)

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

## ⚙️ Configuration / Configuração

- `OpenAIConfig` – Manages OpenAI API key, URL, model, and temperature / Gerencia chave, URL, modelo e temperatura da API OpenAI.  
- `SecurityConfig` and `JwtAuthFilter` – Security filters and JWT authentication / Filtros de segurança e autenticação JWT.  

---

## 🐳 Docker

The project is already Docker-ready. You can build and run the container using the provided `Dockerfile` and `docker-compose.yml`.  

**Pronto para build e testes via Docker.**  

---

## 📈 Next Steps / Próximos Passos

- Deploy to cloud or server (AWS, Azure, or any preferred provider) / Deploy em nuvem ou servidor (AWS, Azure ou outro de preferência).  

---

## 📌 Project Details / Detalhes do Projeto

- **Java version / Versão do Java:** 17  
- **Spring Boot version / Versão do Spring Boot:** 3.5.3  
- **Database / Banco de Dados:** MySQL  
- **Port / Porta:** 8081  

---

## 💡 Why This Project Matters / Por que este projeto é incrível

**English:**  
This project is more than just a chatbot backend: it demonstrates mastery of modern Java development, secure authentication, database integration, Docker deployment readiness, and API best practices. It’s a perfect showcase of technical skills for recruiters.  

**Português:**  
Este projeto vai além de um simples backend de chatbot: demonstra domínio completo de desenvolvimento moderno em Java, autenticação segura, integração com banco de dados, preparação para deployment via Docker e boas práticas de API. É uma vitrine perfeita de habilidades técnicas para recrutadores.


