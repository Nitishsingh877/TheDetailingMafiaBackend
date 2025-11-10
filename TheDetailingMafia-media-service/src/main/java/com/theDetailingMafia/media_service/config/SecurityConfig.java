package com.theDetailingMafia.media_service.config;


import com.theDetailingMafia.media_service.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(a -> a.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // A stateless system does not retain any information about previous interactions.

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/media/display/**", "/api/media/entity/**", "/api/media/debug/**", "/api/media/test/**", "/api/media/cleanup/**").permitAll()
                        .requestMatchers("/api/media/profile/upload").hasAnyRole("CUSTOMER", "WASHER")
                        .requestMatchers("/api/media/car/upload").hasRole("CUSTOMER")
                        .requestMatchers("/api/media/service/**").hasRole("WASHER")
                        .requestMatchers("/api/media/upload").hasRole("CUSTOMER")
                        .requestMatchers("/api/media/health","/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

