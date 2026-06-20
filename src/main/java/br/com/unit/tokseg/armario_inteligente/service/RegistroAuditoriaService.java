package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.model.RegistroAuditoria;
import br.com.unit.tokseg.armario_inteligente.repository.RegistroAuditoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RegistroAuditoriaService {

    private static final Logger logger = LoggerFactory.getLogger(RegistroAuditoriaService.class);
    private final RegistroAuditoriaRepository registroAuditoriaRepository;

    public RegistroAuditoriaService(RegistroAuditoriaRepository registroAuditoriaRepository) {
        this.registroAuditoriaRepository = registroAuditoriaRepository;
    }

    @Transactional
    public RegistroAuditoria salvar(RegistroAuditoria registro) {
        if (registro == null) {
            throw new IllegalArgumentException("Registro de auditoria não pode ser nulo");
        }
        if (registro.getAcao() == null || registro.getAcao().trim().isEmpty()) {
            throw new IllegalArgumentException("Ação do registro não pode ser nula ou vazia");
        }
        if (registro.getDataHora() == null) {
            throw new IllegalArgumentException("Data/hora do registro não pode ser nula");
        }

        logger.info("Registrando ação: {}", registro.getAcao());
        return registroAuditoriaRepository.save(registro);
    }

    public List<RegistroAuditoria> listarTodos() {
        logger.debug("Listando todos os registros de auditoria");
        return registroAuditoriaRepository.findAll();
    }

    public Optional<RegistroAuditoria> buscarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do registro não pode ser nulo");
        }
        logger.debug("Buscando registro de auditoria com ID: {}", id);
        return registroAuditoriaRepository.findById(id);
    }

    @Transactional
    public void remover(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do registro não pode ser nulo");
        }

        if (!registroAuditoriaRepository.existsById(id)) {
            throw new EntityNotFoundException("Registro de auditoria não encontrado com ID: " + id);
        }

        logger.info("Removendo registro de auditoria com ID: {}", id);
        registroAuditoriaRepository.deleteById(id);
    }
}
