package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.model.RegistroAuditoria;
import br.com.unit.tokseg.armario_inteligente.repository.RegistroAuditoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroAuditoriaServiceTest {

    @Mock
    private RegistroAuditoriaRepository registroAuditoriaRepository;

    @InjectMocks
    private RegistroAuditoriaService registroAuditoriaService;

    @Test
    void salvarRejeitaRegistroSemAcao() {
        RegistroAuditoria registro = new RegistroAuditoria();
        registro.setDataHora(LocalDateTime.now());

        assertThatThrownBy(() -> registroAuditoriaService.salvar(registro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ação");
    }

    @Test
    void salvarPersisteRegistroValido() {
        RegistroAuditoria registro = new RegistroAuditoria();
        registro.setAcao("LOGIN");
        registro.setDetalhes("Usuário autenticado");
        registro.setDataHora(LocalDateTime.now());

        when(registroAuditoriaRepository.save(any(RegistroAuditoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegistroAuditoria salvo = registroAuditoriaService.salvar(registro);

        assertThat(salvo.getAcao()).isEqualTo("LOGIN");
        assertThat(salvo.getDetalhes()).isEqualTo("Usuário autenticado");
    }

    @Test
    void removerExcluiRegistroExistente() {
        when(registroAuditoriaRepository.existsById(1)).thenReturn(true);

        registroAuditoriaService.remover(1);

        verify(registroAuditoriaRepository).deleteById(1);
    }

    @Test
    void removerRejeitaRegistroInexistente() {
        when(registroAuditoriaRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> registroAuditoriaService.remover(99))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
