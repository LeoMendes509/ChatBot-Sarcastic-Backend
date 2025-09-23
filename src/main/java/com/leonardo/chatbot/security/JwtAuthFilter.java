package com.leonardo.chatbot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final SecurityJWT securityJWT;

    // 🔹 Endpoints públicos
    private final List<String> publicEndpoints = List.of(
            "/api/users/register",
            "/api/users/login"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthFilter(SecurityJWT securityJWT) {
        this.securityJWT = securityJWT;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 🔹 Ignora endpoints públicos
        for (String endpoint : publicEndpoints) {
            if (pathMatcher.match(endpoint, path)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 🔹 Pega header Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (securityJWT.validateJwtToken(token)) {
                String username = securityJWT.getUsernameFromToken(token);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, null);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header missing");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
