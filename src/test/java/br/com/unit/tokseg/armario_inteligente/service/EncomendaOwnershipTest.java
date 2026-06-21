package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.model.Encomenda;
import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import br.com.unit.tokseg.armario_inteligente.repository.ArmarioRepository;
import br.com.unit.tokseg.armario_inteligente.repository.CompartimentoRepository;
import br.com.unit.tokseg.armario_inteligente.repository.EncomendaRepository;
import br.com.unit.tokseg.armario_inteligente.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncomendaOwnershipTest {

    @Mock
    private EncomendaRepository encomendaRepository;
    @Mock
    private ArmarioRepository armarioRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CompartimentoRepository compartimentoRepository;

    @InjectMocks
    private EncomendaService encomendaService;

    @Test
    void moradorNaoVeEncomendaDeOutroUsuario() {
        UUID moradorId = UUID.randomUUID();
        UUID outroId = UUID.randomUUID();

        Usuario morador = usuario(moradorId, TipoUsuarioEnum.MORADOR);
        Usuario outro = usuario(outroId, TipoUsuarioEnum.MORADOR);

        Encomenda encomenda = new Encomenda();
        encomenda.setIdEncomenda("E1");
        encomenda.setUsuario(outro);

        when(encomendaRepository.findById("E1")).thenReturn(Optional.of(encomenda));

        assertThat(encomendaService.buscarPorId("E1", morador)).isEmpty();
    }

    @Test
    void moradorListaApenasSuasEncomendas() {
        UUID moradorId = UUID.randomUUID();
        Usuario morador = usuario(moradorId, TipoUsuarioEnum.MORADOR);

        Encomenda encomenda = new Encomenda();
        encomenda.setIdEncomenda("E1");
        when(encomendaRepository.findByUsuarioId(moradorId)).thenReturn(List.of(encomenda));

        assertThat(encomendaService.listarParaUsuario(morador)).hasSize(1);
    }

    private Usuario usuario(UUID id, TipoUsuarioEnum tipo) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setTipo(tipo);
        usuario.setEmail(id + "@teste.com");
        return usuario;
    }
}
