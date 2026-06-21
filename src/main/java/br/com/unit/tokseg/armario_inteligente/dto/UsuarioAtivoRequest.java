package br.com.unit.tokseg.armario_inteligente.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioAtivoRequest(@NotNull Boolean ativo) {
}
