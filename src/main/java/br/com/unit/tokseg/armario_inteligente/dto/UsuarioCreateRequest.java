package br.com.unit.tokseg.armario_inteligente.dto;

import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioCreateRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String senha,
        @NotBlank String telefone,
        @NotNull TipoUsuarioEnum tipo
) {
}
