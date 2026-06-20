package br.com.unit.tokseg.armario_inteligente.dto;

import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String senha,
        String telefone,
        TipoUsuarioEnum tipo
) {
}
