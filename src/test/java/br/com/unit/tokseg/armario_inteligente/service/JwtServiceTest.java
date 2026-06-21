package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.config.JwtProperties;
import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
                3_600_000L
        );
        jwtService = new JwtService(properties);
    }

    @Test
    void geraTokenValidoParaUsuario() {
        Usuario usuario = usuario("jwt@teste.com");

        String token = jwtService.generateToken(usuario);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("jwt@teste.com");
        assertThat(jwtService.isTokenValid(token, usuario)).isTrue();
    }

    @Test
    void rejeitaTokenDeOutroUsuario() {
        Usuario usuario = usuario("owner@teste.com");
        Usuario outro = usuario("other@teste.com");

        String token = jwtService.generateToken(usuario);

        assertThat(jwtService.isTokenValid(token, outro)).isFalse();
    }

    private Usuario usuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail(email);
        usuario.setSenha("hash");
        usuario.setNome("Teste");
        usuario.setTelefone("11999999999");
        usuario.setTipo(TipoUsuarioEnum.MORADOR);
        usuario.setAtivo(true);
        return usuario;
    }
}
