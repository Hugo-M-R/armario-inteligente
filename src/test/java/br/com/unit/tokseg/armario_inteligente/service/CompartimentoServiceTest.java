package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.model.Armario;
import br.com.unit.tokseg.armario_inteligente.model.ArmarioStatus;
import br.com.unit.tokseg.armario_inteligente.model.Compartimento;
import br.com.unit.tokseg.armario_inteligente.repository.CompartimentoRepository;
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
class CompartimentoServiceTest {

    @Mock
    private CompartimentoRepository compartimentoRepository;

    @InjectMocks
    private CompartimentoService compartimentoService;

    @Test
    void salvarRejeitaCompartimentoSemArmario() {
        Compartimento compartimento = new Compartimento();

        assertThatThrownBy(() -> compartimentoService.salvar(compartimento))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Armário");
    }

    @Test
    void salvarPersisteCompartimentoValido() {
        Armario armario = new Armario();
        armario.setId(UUID.randomUUID());
        armario.setNumero("A1");
        armario.setStatus(ArmarioStatus.DISPONIVEL);
        armario.setLocalizacao("Hall");

        Compartimento compartimento = new Compartimento(armario, false, null);

        when(compartimentoRepository.save(any(Compartimento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Compartimento salvo = compartimentoService.salvar(compartimento);

        assertThat(salvo.getArmario()).isEqualTo(armario);
        assertThat(salvo.isOcupado()).isFalse();
    }

    @Test
    void atualizarOcupacaoAlteraCompartimentoExistente() {
        UUID id = UUID.randomUUID();
        Compartimento compartimento = new Compartimento();
        compartimento.setIdCompartimento(id);
        compartimento.setOcupado(false);

        when(compartimentoRepository.findById(id)).thenReturn(Optional.of(compartimento));
        when(compartimentoRepository.save(any(Compartimento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Compartimento> atualizado = compartimentoService.atualizarOcupacao(id, true);

        assertThat(atualizado).isPresent();
        assertThat(atualizado.get().isOcupado()).isTrue();
    }

    @Test
    void removerExcluiCompartimentoExistente() {
        UUID id = UUID.randomUUID();
        when(compartimentoRepository.existsById(id)).thenReturn(true);

        compartimentoService.remover(id);

        verify(compartimentoRepository).deleteById(id);
    }

    @Test
    void removerRejeitaCompartimentoInexistente() {
        UUID id = UUID.randomUUID();
        when(compartimentoRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> compartimentoService.remover(id))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
