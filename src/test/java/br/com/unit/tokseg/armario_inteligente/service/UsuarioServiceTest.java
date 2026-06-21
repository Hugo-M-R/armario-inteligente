package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.dto.UsuarioCreateRequest;
import br.com.unit.tokseg.armario_inteligente.exception.ForbiddenOperationException;
import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import br.com.unit.tokseg.armario_inteligente.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void criarRejeitaEmailDuplicado() {
        Usuario admin = admin();
        UsuarioCreateRequest request = new UsuarioCreateRequest(
                "Novo", "novo@teste.com", "senha123", "11999999999", TipoUsuarioEnum.MORADOR);

        when(usuarioRepository.existsByEmail("novo@teste.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.criar(request, admin))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email já cadastrado");
    }

    @Test
    void porteiroSoPodeCriarMorador() {
        Usuario porteiro = usuario(TipoUsuarioEnum.PORTEIRO);
        UsuarioCreateRequest request = new UsuarioCreateRequest(
                "Admin", "admin@teste.com", "senha123", "11999999999", TipoUsuarioEnum.ADMIN);

        when(usuarioRepository.existsByEmail("admin@teste.com")).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.criar(request, porteiro))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessageContaining("Porteiros só podem criar usuários do tipo MORADOR");
    }

    @Test
    void moradorNaoVeOutroUsuario() {
        UUID moradorId = UUID.randomUUID();
        UUID outroId = UUID.randomUUID();

        Usuario morador = usuario(TipoUsuarioEnum.MORADOR);
        morador.setId(moradorId);

        Usuario outro = usuario(TipoUsuarioEnum.MORADOR);
        outro.setId(outroId);

        when(usuarioRepository.findById(outroId)).thenReturn(Optional.of(outro));

        assertThat(usuarioService.buscarPorIdComOwnership(outroId, morador)).isEmpty();
    }

    @Test
    void atualizarAtivoAlteraStatus() {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuario(TipoUsuarioEnum.MORADOR);
        usuario.setId(id);
        usuario.setAtivo(true);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario atualizado = usuarioService.atualizarAtivo(id, false);

        assertThat(atualizado.isAtivo()).isFalse();
    }

    @Test
    void removerExcluiUsuarioExistente() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.existsById(id)).thenReturn(true);

        usuarioService.remover(id);

        verify(usuarioRepository).deleteById(id);
    }

    @Test
    void removerRejeitaUsuarioInexistente() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.remover(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private Usuario admin() {
        return usuario(TipoUsuarioEnum.ADMIN);
    }

    private Usuario usuario(TipoUsuarioEnum tipo) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setTipo(tipo);
        usuario.setEmail(UUID.randomUUID() + "@teste.com");
        return usuario;
    }
}
