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
import java.util.stream.Stream;

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
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/health"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/authenticate").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
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
        configuration.setAllowedOrigins(resolveAllowedOrigins(cors));
        configuration.setAllowedMethods(resolveAllowedMethods(cors));
        configuration.setAllowedHeaders(resolveAllowedHeaders(cors));
        configuration.setAllowCredentials(cors == null || cors.allowCredentials());
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static List<String> resolveAllowedOrigins(SecurityProperties.Cors cors) {
        List<String> configured = cors != null ? cors.allowedOrigins() : null;
        List<String> origins = filterBlank(configured);
        if (origins.isEmpty()) {
            return List.of("http://localhost:3000", "http://127.0.0.1:3000");
        }
        return origins;
    }

    private static List<String> resolveAllowedMethods(SecurityProperties.Cors cors) {
        List<String> configured = cors != null ? cors.allowedMethods() : null;
        List<String> methods = filterBlank(configured);
        if (methods.isEmpty()) {
            return List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        }
        return methods;
    }

    private static List<String> resolveAllowedHeaders(SecurityProperties.Cors cors) {
        List<String> configured = cors != null ? cors.allowedHeaders() : null;
        List<String> headers = filterBlank(configured);
        if (headers.isEmpty()) {
            return List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With");
        }
        return headers;
    }

    private static List<String> filterBlank(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream()
                .flatMap(value -> Stream.of(value.split(",")))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
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
