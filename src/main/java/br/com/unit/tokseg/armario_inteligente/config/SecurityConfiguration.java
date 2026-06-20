package br.com.unit.tokseg.armario_inteligente.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthRateLimitFilter authRateLimitFilter;
    private final UserDetailsService userDetailsService;
    private final SecurityProperties securityProperties;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthFilter,
            AuthRateLimitFilter authRateLimitFilter,
            UserDetailsService userDetailsService,
            SecurityProperties securityProperties
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authRateLimitFilter = authRateLimitFilter;
        this.userDetailsService = userDetailsService;
        this.securityProperties = securityProperties;
    }

    @Bean
    @Profile("!test")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/v1/auth/**", "/h2-console/**").permitAll()
                .requestMatchers("/api/armarios/**").hasAnyRole("ADMIN", "PORTEIRO", "MORADOR")
                .requestMatchers("/api/encomendas/**").hasAnyRole("ADMIN", "PORTEIRO", "MORADOR")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(daoAuthenticationProvider())
            .addFilterBefore(authRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> {
                headers.contentTypeOptions(contentType -> {});
                headers.frameOptions(frame -> frame.sameOrigin());
                headers.xssProtection(xss -> {});
                headers.contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"));
                SecurityProperties.Headers headerConfig = securityProperties.headers();
                if (headerConfig != null && headerConfig.hstsEnabled()) {
                    headers.httpStrictTransportSecurity(hsts -> hsts
                            .maxAgeInSeconds(headerConfig.hstsMaxAgeSeconds())
                            .includeSubDomains(true)
                            .preload(true));
                }
            });

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        SecurityProperties.Cors cors = securityProperties.cors();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(cors != null ? cors.allowedOrigins() : List.of("http://localhost:3000"));
        configuration.setAllowedMethods(cors != null ? cors.allowedMethods() : List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(cors != null ? cors.allowedHeaders() : List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(cors != null && cors.allowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
