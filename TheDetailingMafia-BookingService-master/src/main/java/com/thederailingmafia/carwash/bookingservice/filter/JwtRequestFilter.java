package com.thederailingmafia.carwash.bookingservice.filter;

import com.thederailingmafia.carwash.bookingservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired(required = false)
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("OrderService raw header: " + authHeader);
        String email = null;
        String jwtToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            // System.out.println("OrderService extracted token: " + jwtToken);
            try {
                email = jwtUtil.getEmailFromToken(jwtToken);
                // System.out.println("OrderService extracted email: " + email);
            } catch (Exception e) {
                System.out.println("OrderService failed to extract email: " + e.getMessage());
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            // System.out.println("OrderService loaded UserDetails: " + (userDetails != null ? userDetails.getUsername() + ", " + userDetails.getAuthorities() : "null"));
            if (userDetails != null && jwtUtil.validateToken(jwtToken, email)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email, null, userDetails.getAuthorities() // Ensure ROLE_WASHER
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                //  System.out.println("OrderService set Authentication: " + email + ", Authorities: " + userDetails.getAuthorities());
            }
        }
        filterChain.doFilter(request, response);
    }
}