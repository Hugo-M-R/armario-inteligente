package br.com.unit.tokseg.armario_inteligente.config;

import io.jsonwebtoken.io.Decoders;
import org.springframework.stereotype.Component;

@Component
public class JwtSecretValidator {

    public JwtSecretValidator(JwtProperties jwtProperties) {
        validate(jwtProperties.secret());
    }

    private static void validate(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET é obrigatório");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret.trim());
            if (keyBytes.length < 32) {
                throw new IllegalStateException(
                        "JWT_SECRET deve ser Base64 com no mínimo 256 bits (32 bytes). "
                                + "Gere com: openssl rand -base64 32");
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "JWT_SECRET deve ser uma string Base64 válida. Gere com: openssl rand -base64 32", ex);
        }
    }
}
