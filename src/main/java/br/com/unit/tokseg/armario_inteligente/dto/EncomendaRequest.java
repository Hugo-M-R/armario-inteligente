package br.com.unit.tokseg.armario_inteligente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EncomendaRequest(
        @NotBlank String idEncomenda,
        @NotBlank String descricao,
        @NotBlank String remetente,
        @NotNull UUID armarioId,
        @NotNull UUID usuarioId
) {
}
