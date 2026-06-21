package br.com.unit.tokseg.armario_inteligente.dto;

import br.com.unit.tokseg.armario_inteligente.model.Notificacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificacaoResponse(
        String idNotificacao,
        UUID usuarioId,
        String mensagem,
        LocalDateTime dataEnvio,
        boolean lida
) {
    public static NotificacaoResponse from(Notificacao notificacao) {
        return new NotificacaoResponse(
                notificacao.getIdNotificacao(),
                notificacao.getUsuario() != null ? notificacao.getUsuario().getId() : null,
                notificacao.getMensagem(),
                notificacao.getDataEnvio(),
                notificacao.isLida()
        );
    }
}
