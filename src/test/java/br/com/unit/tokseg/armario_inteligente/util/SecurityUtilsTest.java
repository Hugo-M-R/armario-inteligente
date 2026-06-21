package br.com.unit.tokseg.armario_inteligente.util;

import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityUtilsTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsuarioRetornaUsuarioAutenticado() {
        Usuario usuario = usuario(TipoUsuarioEnum.MORADOR);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities())
        );

        assertThat(SecurityUtils.getCurrentUsuario()).isEqualTo(usuario);
    }

    @Test
    void getCurrentUsuarioRejeitaContextoVazio() {
        assertThatThrownBy(SecurityUtils::getCurrentUsuario)
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void canManageAllEncomendasParaAdminEPorteiro() {
        assertThat(SecurityUtils.canManageAllEncomendas(usuario(TipoUsuarioEnum.ADMIN))).isTrue();
        assertThat(SecurityUtils.canManageAllEncomendas(usuario(TipoUsuarioEnum.PORTEIRO))).isTrue();
        assertThat(SecurityUtils.canManageAllEncomendas(usuario(TipoUsuarioEnum.MORADOR))).isFalse();
    }

    private Usuario usuario(TipoUsuarioEnum tipo) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setTipo(tipo);
        usuario.setEmail(UUID.randomUUID() + "@teste.com");
        return usuario;
    }
}
