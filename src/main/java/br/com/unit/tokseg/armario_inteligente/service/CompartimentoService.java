package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.model.Compartimento;
import br.com.unit.tokseg.armario_inteligente.repository.CompartimentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompartimentoService {

    private static final Logger logger = LoggerFactory.getLogger(CompartimentoService.class);
    private final CompartimentoRepository compartimentoRepository;

    public CompartimentoService(CompartimentoRepository compartimentoRepository) {
        this.compartimentoRepository = compartimentoRepository;
    }

    public List<Compartimento> listarTodos() {
        logger.debug("Listando todos os compartimentos");
        return compartimentoRepository.findAll();
    }

    public Optional<Compartimento> buscarPorId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do compartimento não pode ser nulo");
        }
        logger.debug("Buscando compartimento com ID: {}", id);
        return compartimentoRepository.findById(id);
    }

    @Transactional
    public Compartimento salvar(Compartimento compartimento) {
        if (compartimento == null) {
            throw new IllegalArgumentException("Compartimento não pode ser nulo");
        }
        if (compartimento.getArmario() == null) {
            throw new IllegalArgumentException("Armário não pode ser nulo");
        }

        logger.info("Salvando novo compartimento para o armário: {}", compartimento.getArmario().getId());
        return compartimentoRepository.save(compartimento);
    }

    @Transactional
    public void remover(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do compartimento não pode ser nulo");
        }
        
        if (!compartimentoRepository.existsById(id)) {
            throw new EntityNotFoundException("Compartimento não encontrado com ID: " + id);
        }

        logger.info("Removendo compartimento com ID: {}", id);
        compartimentoRepository.deleteById(id);
    }

    @Transactional
    public Optional<Compartimento> atualizarOcupacao(UUID id, boolean ocupado) {
        if (id == null) {
            throw new IllegalArgumentException("ID do compartimento não pode ser nulo");
        }

        logger.info("Atualizando status de ocupação do compartimento {} para {}", id, ocupado);
        return compartimentoRepository.findById(id)
                .map(compartimento -> {
                    compartimento.setOcupado(ocupado);
                    return compartimentoRepository.save(compartimento);
                });
    }
}
