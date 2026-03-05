package com.example.ContactHub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Разрешить доступ к Swagger UI и API документации без аутентификации
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()
                        //чтобы API тоже было открыто — раскомментируй:
                        .requestMatchers("/api/**").permitAll()
                        // А если хочешь, чтобы API осталось защищённым — оставь .authenticated()
                        //.requestMatchers("/api/**").authenticated()
                        // Любые другие запросы
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                // Отключаем CSRF для простоты тестирования через Swagger
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
