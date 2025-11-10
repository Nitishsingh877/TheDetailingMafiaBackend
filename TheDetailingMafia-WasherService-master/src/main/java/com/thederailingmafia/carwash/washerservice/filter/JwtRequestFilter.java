package com.thederailingmafia.carwash.washerservice.filter;

import com.thederailingmafia.carwash.washerservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("Raw Authorization header: " + authHeader);

        String email = null;
        String jwtToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            System.out.println("Extracted token: " + jwtToken);
            try {
                email = jwtUtil.getEmailFromToken(jwtToken);
                System.out.println("Extracted email: " + email);
            } catch (Exception e) {
                System.out.println("Failed to extract email from token: " + e.getMessage());
            }
        } else {
            System.out.println("No valid Bearer token in header");
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtUtil.validateToken(jwtToken, email)) {
                    // Extract authorities directly from JWT token
                    List<String> roles = jwtUtil.getRolesFromToken(jwtToken);
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> {
                                // Normalize role format - ensure ROLE_ prefix
                                String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                                return new SimpleGrantedAuthority(normalizedRole.replace("ROLE_ROLE_", "ROLE_"));
                            })
                            .collect(Collectors.toList());

                    System.out.println("JWT Authorities: " + authorities);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            email, null, authorities
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Set Authentication in context for: " + email + ", Authorities: " + authorities);
                } else {
                    System.out.println("Token validation failed for email: " + email);
                }
            } catch (Exception e) {
                System.out.println("Error processing JWT token: " + e.getMessage());
            }
        } else if (email != null) {
            System.out.println("Authentication already exists in context");
        }

        filterChain.doFilter(request, response);
    }
}