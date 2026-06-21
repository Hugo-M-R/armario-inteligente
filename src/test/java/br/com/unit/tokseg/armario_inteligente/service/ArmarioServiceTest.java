package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.model.Armario;
import br.com.unit.tokseg.armario_inteligente.model.ArmarioStatus;
import br.com.unit.tokseg.armario_inteligente.repository.ArmarioRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArmarioServiceTest {

    @Mock
    private ArmarioRepository armarioRepository;

    @InjectMocks
    private ArmarioService armarioService;

    @Test
    void salvarRejeitaArmarioSemLocalizacao() {
        Armario armario = new Armario();
        armario.setNumero("A1");
        armario.setStatus(ArmarioStatus.DISPONIVEL);

        assertThatThrownBy(() -> armarioService.salvar(armario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Localização");
    }

    @Test
    void salvarPersisteArmarioValido() {
        Armario armario = new Armario();
        armario.setNumero("A1");
        armario.setStatus(ArmarioStatus.DISPONIVEL);
        armario.setLocalizacao("Hall");

        when(armarioRepository.save(any(Armario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Armario salvo = armarioService.salvar(armario);

        assertThat(salvo.getNumero()).isEqualTo("A1");
        assertThat(salvo.getStatus()).isEqualTo(ArmarioStatus.DISPONIVEL);
    }

    @Test
    void atualizarStatusAlteraArmarioExistente() {
        UUID id = UUID.randomUUID();
        Armario armario = new Armario();
        armario.setId(id);
        armario.setNumero("A1");
        armario.setStatus(ArmarioStatus.DISPONIVEL);
        armario.setLocalizacao("Hall");

        when(armarioRepository.findById(id)).thenReturn(Optional.of(armario));
        when(armarioRepository.save(any(Armario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Armario> atualizado = armarioService.atualizarStatus(id, ArmarioStatus.OCUPADO);

        assertThat(atualizado).isPresent();
        assertThat(atualizado.get().getStatus()).isEqualTo(ArmarioStatus.OCUPADO);
    }

    @Test
    void buscarPorStatusRetornaLista() {
        Armario armario = new Armario();
        armario.setStatus(ArmarioStatus.DISPONIVEL);

        when(armarioRepository.findByStatus(ArmarioStatus.DISPONIVEL)).thenReturn(List.of(armario));

        assertThat(armarioService.buscarPorStatus(ArmarioStatus.DISPONIVEL)).hasSize(1);
    }

    @Test
    void contarPorStatusDelegaAoRepositorio() {
        when(armarioRepository.countByStatus(ArmarioStatus.DISPONIVEL)).thenReturn(3L);

        assertThat(armarioService.contarPorStatus(ArmarioStatus.DISPONIVEL)).isEqualTo(3L);
    }
}
