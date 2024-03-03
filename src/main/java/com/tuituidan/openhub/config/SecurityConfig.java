package com.tuituidan.openhub.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/3/3
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * filterChain
     *
     * @param http http
     * @return SecurityFilterChain
     * @throws Exception Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.authorizeRequests().anyRequest().authenticated();
        http.formLogin(Customizer.withDefaults());
        return http.build();
    }

}
