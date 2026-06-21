package br.com.unit.tokseg.armario_inteligente.dto;

import br.com.unit.tokseg.armario_inteligente.model.Encomenda;
import br.com.unit.tokseg.armario_inteligente.model.StatusRetirada;

import java.time.LocalDateTime;
import java.util.UUID;

public record EncomendaResponse(
        String idEncomenda,
        String descricao,
        String remetente,
        LocalDateTime dataRecebimento,
        UUID armarioId,
        String armarioNumero,
        UUID usuarioId,
        String usuarioEmail,
        StatusRetirada statusRetirada,
        LocalDateTime dataRetirada,
        LocalDateTime dataExpiracaoCodigo
) {
    public static EncomendaResponse from(Encomenda encomenda) {
        return new EncomendaResponse(
                encomenda.getIdEncomenda(),
                encomenda.getDescricao(),
                encomenda.getRemetente(),
                encomenda.getDataRecebimento(),
                encomenda.getArmario() != null ? encomenda.getArmario().getId() : null,
                encomenda.getArmario() != null ? encomenda.getArmario().getNumero() : null,
                encomenda.getUsuario() != null ? encomenda.getUsuario().getId() : null,
                encomenda.getUsuario() != null ? encomenda.getUsuario().getEmail() : null,
                encomenda.getStatusRetirada(),
                encomenda.getDataRetirada(),
                encomenda.getDataExpiracaoCodigo()
        );
    }
}
