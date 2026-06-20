package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.model.Encomenda;
import br.com.unit.tokseg.armario_inteligente.repository.EncomendaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import br.com.unit.tokseg.armario_inteligente.annotation.Auditavel;

@Service
public class EncomendaService {

    private static final Logger logger = LoggerFactory.getLogger(EncomendaService.class);
    private final EncomendaRepository encomendaRepository;

    public EncomendaService(EncomendaRepository encomendaRepository) {
        this.encomendaRepository = encomendaRepository;
    }

    @Auditavel(acao = "CADASTRO_ENCOMENDA", detalhes = "Cadastro de nova encomenda no sistema")
    @Transactional
    public Encomenda salvar(Encomenda encomenda) {
        if (encomenda == null) {
            throw new IllegalArgumentException("Encomenda não pode ser nula");
        }
        if (encomenda.getIdEncomenda() == null || encomenda.getIdEncomenda().trim().isEmpty()) {
            throw new IllegalArgumentException("ID da encomenda não pode ser nulo ou vazio");
        }
        if (encomenda.getDescricao() == null || encomenda.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição da encomenda não pode ser nula ou vazia");
        }
        if (encomenda.getRemetente() == null || encomenda.getRemetente().trim().isEmpty()) {
            throw new IllegalArgumentException("Remetente não pode ser nulo ou vazio");
        }

        logger.info("Salvando nova encomenda com ID: {}", encomenda.getIdEncomenda());
        return encomendaRepository.save(encomenda);
    }

    public List<Encomenda> listarTodas() {
        logger.debug("Listando todas as encomendas");
        return encomendaRepository.findAll();
    }

    public Optional<Encomenda> buscarPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da encomenda não pode ser nulo ou vazio");
        }
        logger.debug("Buscando encomenda com ID: {}", id);
        return encomendaRepository.findById(id);
    }

    @Auditavel(acao = "RETIRADA_ENCOMENDA", detalhes = "Retirada de encomenda do sistema")
    @Transactional
    public void remover(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da encomenda não pode ser nulo ou vazio");
        }
        
        if (!encomendaRepository.existsById(id)) {
            throw new EntityNotFoundException("Encomenda não encontrada com ID: " + id);
        }

        logger.info("Removendo encomenda com ID: {}", id);
        encomendaRepository.deleteById(id);
    }
}
