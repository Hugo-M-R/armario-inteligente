package br.com.unit.tokseg.armario_inteligente.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import br.com.unit.tokseg.armario_inteligente.service.JwtService;
import br.com.unit.tokseg.armario_inteligente.service.CustomUserDetailsService;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestSecurityConfig {

    @Bean
    @Primary
    JwtService jwtService() {
        return mock(JwtService.class);
    }

    @Bean
    @Primary
    CustomUserDetailsService userDetailsService() {
        return mock(CustomUserDetailsService.class);
    }

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
