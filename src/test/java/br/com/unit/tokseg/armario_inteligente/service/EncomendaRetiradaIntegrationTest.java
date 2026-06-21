package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.dto.CodigoAcessoResponse;
import br.com.unit.tokseg.armario_inteligente.model.Armario;
import br.com.unit.tokseg.armario_inteligente.model.ArmarioStatus;
import br.com.unit.tokseg.armario_inteligente.model.Encomenda;
import br.com.unit.tokseg.armario_inteligente.model.StatusRetirada;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncomendaRetiradaIntegrationTest {

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
    void fluxoGerarValidarRetirar() {
        UUID moradorId = UUID.randomUUID();
        Usuario morador = new Usuario();
        morador.setId(moradorId);
        morador.setTipo(TipoUsuarioEnum.MORADOR);

        Usuario porteiro = new Usuario();
        porteiro.setId(UUID.randomUUID());
        porteiro.setTipo(TipoUsuarioEnum.PORTEIRO);

        Armario armario = new Armario();
        armario.setId(UUID.randomUUID());
        armario.setNumero("A1");
        armario.setStatus(ArmarioStatus.OCUPADO);

        Encomenda encomenda = new Encomenda();
        encomenda.setIdEncomenda("E1");
        encomenda.setUsuario(morador);
        encomenda.setArmario(armario);
        encomenda.setStatusRetirada(StatusRetirada.PENDENTE);

        when(encomendaRepository.findById("E1")).thenReturn(Optional.of(encomenda));
        when(encomendaRepository.save(any(Encomenda.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(compartimentoRepository.findByEncomendaAtualIdEncomenda("E1")).thenReturn(List.of());

        CodigoAcessoResponse codigo = encomendaService.gerarCodigo("E1", porteiro);
        assertThat(codigo.codigo()).isNotBlank();

        encomenda.setCodigoAcesso(codigo.codigo());
        encomenda.setDataExpiracaoCodigo(codigo.dataExpiracao());

        assertThat(encomendaService.validarCodigo("E1", codigo.codigo(), morador)).isTrue();

        Encomenda retirada = encomendaService.retirar("E1", codigo.codigo(), morador);

        assertThat(retirada.getStatusRetirada()).isEqualTo(StatusRetirada.RETIRADA);
        assertThat(retirada.getDataRetirada()).isBefore(LocalDateTime.now().plusSeconds(1));
        verify(armarioRepository).save(armario);
        assertThat(armario.getStatus()).isEqualTo(ArmarioStatus.DISPONIVEL);
    }
}
