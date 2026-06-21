package br.com.unit.tokseg.armario_inteligente.dto;

import java.time.LocalDateTime;

public record CodigoAcessoResponse(String codigo, LocalDateTime dataExpiracao) {
}
