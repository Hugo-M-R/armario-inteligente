package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.dto.RegisterRequest;
import br.com.unit.tokseg.armario_inteligente.dto.UsuarioResponse;
import br.com.unit.tokseg.armario_inteligente.exception.ForbiddenOperationException;
import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import br.com.unit.tokseg.armario_inteligente.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserRetornaUsuarioAutenticado() {
        Usuario usuario = new Usuario();
        usuario.setId(java.util.UUID.randomUUID());
        usuario.setNome("Morador");
        usuario.setEmail("morador@teste.com");
        usuario.setTelefone("11999999999");
        usuario.setTipo(TipoUsuarioEnum.MORADOR);
        usuario.setAtivo(true);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities())
        );

        UsuarioResponse response = authenticationService.getCurrentUser();

        assertThat(response.email()).isEqualTo("morador@teste.com");
        assertThat(response.tipo()).isEqualTo(TipoUsuarioEnum.MORADOR);
    }

    @Test
    void registerForcaTipoMorador() {
        RegisterRequest request = new RegisterRequest(
                "Morador", "morador@teste.com", "senha123", "11999999999", null);
        when(usuarioRepository.findByEmail("morador@teste.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario registrado = authenticationService.register(request);

        assertThat(registrado.getTipo()).isEqualTo(TipoUsuarioEnum.MORADOR);
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    void registerRejeitaTipoAdmin() {
        RegisterRequest request = new RegisterRequest(
                "Admin", "admin@teste.com", "senha123", "11999999999", TipoUsuarioEnum.ADMIN);

        assertThatThrownBy(() -> authenticationService.register(request))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void registerCodificaSenha() {
        RegisterRequest request = new RegisterRequest(
                "Morador", "m@teste.com", "plain", "11999999999", TipoUsuarioEnum.MORADOR);
        when(usuarioRepository.findByEmail("m@teste.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain")).thenReturn("$2a$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authenticationService.register(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getSenha()).isEqualTo("$2a$hash");
    }
}
