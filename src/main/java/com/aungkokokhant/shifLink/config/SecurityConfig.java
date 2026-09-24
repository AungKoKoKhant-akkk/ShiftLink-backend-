package com.aungkokokhant.shifLink.config;

import com.aungkokokhant.shifLink.employee.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)

            throws Exception {


        return http
                .authenticationProvider(authenticationProvider())
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )

                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth

                        // Local development only: H2 Console
                        .requestMatchers("/h2-console/**").permitAll()

                        // Authentication
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/logout"
                        ).permitAll()

                        .requestMatchers("/api/auth/me").authenticated()

                        // Employee management
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/employees/**"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers("/api/employees/**")
                        .hasRole("ADMIN")

                        // Own data: Admin / Manager / User
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/shifts/my",
                                "/api/swap-requests/my"
                        ).hasAnyRole("ADMIN", "MANAGER", "USER")

                        // All data: Admin / Manager only
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/shifts",
                                "/api/swap-requests"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        // Shift create / update / delete
                        .requestMatchers("/api/shifts/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // User can submit own swap request
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/swap-requests"
                        ).hasAnyRole("ADMIN", "MANAGER", "USER")

                        // Approve / reject swap request
                        .requestMatchers("/api/swap-requests/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        .anyRequest().authenticated()
                )

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(customUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
