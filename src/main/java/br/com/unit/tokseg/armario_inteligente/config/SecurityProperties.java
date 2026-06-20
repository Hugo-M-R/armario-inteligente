package br.com.unit.tokseg.armario_inteligente.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security")
public record SecurityProperties(
        Cors cors,
        Headers headers,
        RateLimit rateLimit
) {
    public record Cors(
            List<String> allowedOrigins,
            List<String> allowedMethods,
            List<String> allowedHeaders,
            boolean allowCredentials
    ) {
    }

    public record Headers(
            boolean hstsEnabled,
            long hstsMaxAgeSeconds
    ) {
    }

    public record RateLimit(
            boolean enabled,
            int maxRequests,
            long windowSeconds
    ) {
    }
}

