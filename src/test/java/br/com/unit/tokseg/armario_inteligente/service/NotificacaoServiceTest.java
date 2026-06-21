package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.model.Notificacao;
import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import br.com.unit.tokseg.armario_inteligente.repository.NotificacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @InjectMocks
    private NotificacaoService notificacaoService;

    @Test
    void salvarRejeitaMensagemVazia() {
        Usuario morador = usuario(TipoUsuarioEnum.MORADOR);
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(morador);
        notificacao.setMensagem("   ");

        assertThatThrownBy(() -> notificacaoService.salvar(notificacao))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Mensagem");
    }

    @Test
    void marcarComoLidaAtualizaNotificacao() {
        UUID moradorId = UUID.randomUUID();
        Usuario morador = usuario(TipoUsuarioEnum.MORADOR);
        morador.setId(moradorId);

        Notificacao notificacao = new Notificacao();
        notificacao.setIdNotificacao("N1");
        notificacao.setUsuario(morador);
        notificacao.setMensagem("Sua encomenda chegou");
        notificacao.setLida(false);

        when(notificacaoRepository.findById("N1")).thenReturn(Optional.of(notificacao));
        when(notificacaoRepository.save(any(Notificacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Notificacao> resultado = notificacaoService.marcarComoLida("N1", morador);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().isLida()).isTrue();
    }

    @Test
    void marcarComoLidaRetornaVazioQuandoNaoEncontrada() {
        Usuario morador = usuario(TipoUsuarioEnum.MORADOR);
        morador.setId(UUID.randomUUID());

        when(notificacaoRepository.findById("N999")).thenReturn(Optional.empty());

        assertThat(notificacaoService.marcarComoLida("N999", morador)).isEmpty();
    }

    @Test
    void removerExcluiNotificacaoExistente() {
        when(notificacaoRepository.existsById("N1")).thenReturn(true);

        notificacaoService.remover("N1");

        verify(notificacaoRepository).deleteById("N1");
    }

    @Test
    void removerRejeitaNotificacaoInexistente() {
        when(notificacaoRepository.existsById("N999")).thenReturn(false);

        assertThatThrownBy(() -> notificacaoService.remover("N999"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private Usuario usuario(TipoUsuarioEnum tipo) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setTipo(tipo);
        usuario.setEmail("user@teste.com");
        return usuario;
    }
}
