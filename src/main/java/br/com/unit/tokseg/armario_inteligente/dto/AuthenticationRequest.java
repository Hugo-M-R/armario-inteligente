package br.com.unit.tokseg.armario_inteligente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank @Email String email,
        @NotBlank String senha
) {
}
