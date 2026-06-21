package br.com.unit.tokseg.armario_inteligente.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidarCodigoRequest(@NotBlank String codigo) {
}
