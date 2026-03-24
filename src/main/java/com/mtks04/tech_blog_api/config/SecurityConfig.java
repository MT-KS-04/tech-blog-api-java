package com.mtks04.tech_blog_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                    .anyRequest().authenticated()
                )
                .formLogin(form -> form 
                    .loginPage("/login")
                    .defaultSuccessUrl("/home", true)
                    .permitAll()
                )
                .logout(logout -> logout.permitAll());
        return httpSecurity.build();

    }
}
