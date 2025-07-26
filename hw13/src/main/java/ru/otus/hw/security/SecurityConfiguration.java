package ru.otus.hw.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(c -> c
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/authors", "/books", "/genres").authenticated()
                        .requestMatchers(regexMatcher(HttpMethod.GET, "/books/\\d+")).authenticated()
                        .requestMatchers(HttpMethod.GET, "/books/create", "/books/edit/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/books").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/books").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/books").hasAuthority("ADMIN")
                )
                .formLogin(Customizer.withDefaults())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
