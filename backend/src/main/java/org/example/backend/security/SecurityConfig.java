package org.example.backend.security;

import org.example.backend.services.CostumeUserService;
import org.example.backend.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {// this class(esspecially SecurityFilterChain) intercepts every incoming HTTP request to your backend before it reaches your controllers.


    private final JwtUtil jwtUtil;
    private final CostumeUserService costumeUserService;

    public SecurityConfig(JwtUtil jwtUtil,CostumeUserService costumeUserService) {
        this.jwtUtil = jwtUtil;
        this.costumeUserService = costumeUserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:4200")); // only your frontend
                    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setExposedHeaders(List.of("Authorization"));
                    config.setAllowCredentials(true); // now you can send auth headers
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // allow unauthenticated access
                        .requestMatchers("/api/jobs/getAllJobs").permitAll()
                        .requestMatchers("/api/jobs/images/**").permitAll()
                        .anyRequest().authenticated() // everything else requires auth
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // JWT filter to check token
        http.addFilterBefore(new JwtAuthFilter(jwtUtil, costumeUserService), UsernamePasswordAuthenticationFilter.class);//UsernamePasswordAuthenticationFilter is the default filter that processes authentication(login) requests in Spring Security.but its not used in this case because we are using JWTs.

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
