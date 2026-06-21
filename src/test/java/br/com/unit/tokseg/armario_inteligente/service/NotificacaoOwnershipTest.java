package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.exception.ForbiddenOperationException;
import br.com.unit.tokseg.armario_inteligente.model.Notificacao;
import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import br.com.unit.tokseg.armario_inteligente.repository.NotificacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacaoOwnershipTest {

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @InjectMocks
    private NotificacaoService notificacaoService;

    @Test
    void porteiroNaoAcessaNotificacoes() {
        Usuario porteiro = usuario(TipoUsuarioEnum.PORTEIRO);

        assertThatThrownBy(() -> notificacaoService.listarParaUsuario(porteiro))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void moradorNaoVeNotificacaoDeOutroUsuario() {
        UUID moradorId = UUID.randomUUID();
        Usuario morador = usuario(TipoUsuarioEnum.MORADOR);
        morador.setId(moradorId);

        Usuario outro = usuario(TipoUsuarioEnum.MORADOR);
        outro.setId(UUID.randomUUID());

        Notificacao notificacao = new Notificacao();
        notificacao.setIdNotificacao("N1");
        notificacao.setUsuario(outro);

        when(notificacaoRepository.findById("N1")).thenReturn(Optional.of(notificacao));

        assertThat(notificacaoService.buscarPorId("N1", morador)).isEmpty();
    }

    @Test
    void moradorListaApenasSuasNotificacoes() {
        UUID moradorId = UUID.randomUUID();
        Usuario morador = usuario(TipoUsuarioEnum.MORADOR);
        morador.setId(moradorId);

        when(notificacaoRepository.findByUsuarioId(moradorId)).thenReturn(List.of(new Notificacao()));

        assertThat(notificacaoService.listarParaUsuario(morador)).hasSize(1);
    }

    private Usuario usuario(TipoUsuarioEnum tipo) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setTipo(tipo);
        usuario.setEmail("user@teste.com");
        return usuario;
    }
}
