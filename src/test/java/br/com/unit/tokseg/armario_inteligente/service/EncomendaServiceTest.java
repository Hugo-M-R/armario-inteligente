package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.dto.EncomendaRequest;
import br.com.unit.tokseg.armario_inteligente.exception.ForbiddenOperationException;
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
import jakarta.persistence.EntityNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncomendaServiceTest {

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
    void criarAssociaArmarioEUsuario() {
        UUID armarioId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        Armario armario = new Armario();
        armario.setId(armarioId);
        armario.setNumero("A1");
        armario.setStatus(ArmarioStatus.DISPONIVEL);
        armario.setLocalizacao("Hall");

        Usuario usuario = usuario(usuarioId, TipoUsuarioEnum.MORADOR);
        EncomendaRequest request = new EncomendaRequest("E1", "Pacote", "Correios", armarioId, usuarioId);

        when(armarioRepository.findById(armarioId)).thenReturn(Optional.of(armario));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(encomendaRepository.save(any(Encomenda.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(armarioRepository.save(any(Armario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Encomenda criada = encomendaService.criar(request);

        assertThat(criada.getIdEncomenda()).isEqualTo("E1");
        assertThat(criada.getStatusRetirada()).isEqualTo(StatusRetirada.PENDENTE);
        assertThat(armario.getStatus()).isEqualTo(ArmarioStatus.OCUPADO);
        assertThat(armario.getEncomendaAtual()).isEqualTo(criada);
    }

    @Test
    void criarRejeitaArmarioInexistente() {
        UUID armarioId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        EncomendaRequest request = new EncomendaRequest("E1", "Pacote", "Correios", armarioId, usuarioId);

        when(armarioRepository.findById(armarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> encomendaService.criar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Armário não encontrado");
    }

    @Test
    void salvarRejeitaEncomendaSemDescricao() {
        Encomenda encomenda = new Encomenda();
        encomenda.setIdEncomenda("E1");
        encomenda.setRemetente("Correios");

        assertThatThrownBy(() -> encomendaService.salvar(encomenda))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Descrição");
    }

    @Test
    void moradorNaoPodeGerarCodigo() {
        Usuario morador = usuario(UUID.randomUUID(), TipoUsuarioEnum.MORADOR);

        assertThatThrownBy(() -> encomendaService.gerarCodigo("E1", morador))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void validarCodigoRejeitaCodigoExpirado() {
        UUID moradorId = UUID.randomUUID();
        Usuario morador = usuario(moradorId, TipoUsuarioEnum.MORADOR);

        Encomenda encomenda = new Encomenda();
        encomenda.setIdEncomenda("E1");
        encomenda.setUsuario(morador);
        encomenda.setStatusRetirada(StatusRetirada.PENDENTE);
        encomenda.setCodigoAcesso("123456");
        encomenda.setDataExpiracaoCodigo(LocalDateTime.now().minusMinutes(1));

        when(encomendaRepository.findById("E1")).thenReturn(Optional.of(encomenda));

        assertThatThrownBy(() -> encomendaService.validarCodigo("E1", "123456", morador))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    void porteiroRetiraSemCodigo() {
        Usuario porteiro = usuario(UUID.randomUUID(), TipoUsuarioEnum.PORTEIRO);
        Usuario morador = usuario(UUID.randomUUID(), TipoUsuarioEnum.MORADOR);

        Armario armario = new Armario();
        armario.setId(UUID.randomUUID());
        armario.setStatus(ArmarioStatus.OCUPADO);

        Encomenda encomenda = new Encomenda();
        encomenda.setIdEncomenda("E1");
        encomenda.setUsuario(morador);
        encomenda.setArmario(armario);
        encomenda.setStatusRetirada(StatusRetirada.PENDENTE);

        when(encomendaRepository.findById("E1")).thenReturn(Optional.of(encomenda));
        when(encomendaRepository.save(any(Encomenda.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(compartimentoRepository.findByEncomendaAtualIdEncomenda("E1")).thenReturn(List.of());

        Encomenda retirada = encomendaService.retirar("E1", null, porteiro);

        assertThat(retirada.getStatusRetirada()).isEqualTo(StatusRetirada.RETIRADA);
        verify(armarioRepository).save(armario);
    }

    @Test
    void removerLiberaArmario() {
        Armario armario = new Armario();
        armario.setId(UUID.randomUUID());
        armario.setStatus(ArmarioStatus.OCUPADO);

        Encomenda encomenda = new Encomenda();
        encomenda.setIdEncomenda("E1");
        encomenda.setArmario(armario);

        when(encomendaRepository.findById("E1")).thenReturn(Optional.of(encomenda));
        when(compartimentoRepository.findByEncomendaAtualIdEncomenda("E1")).thenReturn(List.of());

        encomendaService.remover("E1");

        assertThat(armario.getStatus()).isEqualTo(ArmarioStatus.DISPONIVEL);
        verify(encomendaRepository).deleteById("E1");
    }

    @Test
    void removerRejeitaEncomendaInexistente() {
        when(encomendaRepository.findById("E999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> encomendaService.remover("E999"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private Usuario usuario(UUID id, TipoUsuarioEnum tipo) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setTipo(tipo);
        usuario.setEmail(id + "@teste.com");
        return usuario;
    }
}
