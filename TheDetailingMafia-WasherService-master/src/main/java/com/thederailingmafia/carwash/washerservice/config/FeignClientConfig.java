package com.thederailingmafia.carwash.washerservice.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Forward X-User-Email if present
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String userEmail = request.getHeader("X-User-Email");
                if (userEmail != null) {
                    System.out.println("Feign forwarding X-User-Email: " + userEmail);
                    requestTemplate.header("X-User-Email", userEmail);
                }

                String authHeader = request.getHeader("Authorization");
                System.out.println("Feign raw header from request: " + authHeader);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    System.out.println("Feign forwarding JWT: " + authHeader);
                    requestTemplate.header("Authorization", authHeader);
                    return;
                }
            }

            // Fallback to SecurityContext
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getCredentials() != null) {
                String token = auth.getCredentials().toString();
                if (!token.startsWith("Bearer ")) {
                    token = "Bearer " + token;
                }
                System.out.println("Feign forwarding JWT from SecurityContext: " + token);
                requestTemplate.header("Authorization", token);
            } else {
                System.out.println("No JWT in SecurityContext for Feign");
            }
        };
    }
}