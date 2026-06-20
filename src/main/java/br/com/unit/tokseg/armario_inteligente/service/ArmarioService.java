package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.model.Armario;
import br.com.unit.tokseg.armario_inteligente.model.ArmarioStatus;
import br.com.unit.tokseg.armario_inteligente.repository.ArmarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.unit.tokseg.armario_inteligente.annotation.Auditavel;

@Service
public class ArmarioService {

    private static final Logger logger = LoggerFactory.getLogger(ArmarioService.class);
    private final ArmarioRepository armarioRepository;

    public ArmarioService(ArmarioRepository armarioRepository) {
        this.armarioRepository = armarioRepository;
    }

    public boolean existeNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Número do armário não pode ser nulo ou vazio");
        }
        logger.debug("Verificando existência do armário com número: {}", numero);
        return armarioRepository.existsByNumero(numero);
    }

    @Auditavel(acao = "CADASTRO_ARMARIO", detalhes = "Cadastro de novo armário no sistema")
    @Transactional
    public Armario salvar(Armario armario) {
        if (armario == null) {
            throw new IllegalArgumentException("Armário não pode ser nulo");
        }
        if (armario.getNumero() == null || armario.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("Número do armário não pode ser nulo ou vazio");
        }
        if (armario.getStatus() == null) {
            throw new IllegalArgumentException("Status do armário não pode ser nulo");
        }
        if (armario.getLocalizacao() == null || armario.getLocalizacao().trim().isEmpty()) {
            throw new IllegalArgumentException("Localização do armário não pode ser nula ou vazia");
        }

        logger.info("Salvando novo armário: {}", armario.getNumero());
        return armarioRepository.save(armario);
    }

    public List<Armario> listarTodos() {
        logger.debug("Listando todos os armários");
        return armarioRepository.findAll();
    }

    public List<Armario> buscarPorStatus(ArmarioStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status não pode ser nulo");
        }
        logger.debug("Buscando armários com status: {}", status);
        return armarioRepository.findByStatus(status);
    }

    public List<Armario> buscarPorLocalizacao(String localizacao) {
        if (localizacao == null || localizacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Localização não pode ser nula ou vazia");
        }
        logger.debug("Buscando armários na localização: {}", localizacao);
        return armarioRepository.findByLocalizacao(localizacao);
    }

    public List<Armario> buscarPorStatusELocalizacao(ArmarioStatus status, String localizacao) {
        if (status == null) {
            throw new IllegalArgumentException("Status não pode ser nulo");
        }
        if (localizacao == null || localizacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Localização não pode ser nula ou vazia");
        }
        logger.debug("Buscando armários com status {} e localização {}", status, localizacao);
        return armarioRepository.findByStatusAndLocalizacao(status, localizacao);
    }

    @Auditavel(acao = "ATUALIZACAO_STATUS_ARMARIO", detalhes = "Atualização de status do armário")
    @Transactional
    public Optional<Armario> atualizarStatus(UUID id, ArmarioStatus novoStatus) {
        if (id == null) {
            throw new IllegalArgumentException("ID do armário não pode ser nulo");
        }
        if (novoStatus == null) {
            throw new IllegalArgumentException("Novo status não pode ser nulo");
        }

        logger.info("Atualizando status do armário {} para {}", id, novoStatus);
        return armarioRepository.findById(id)
                .map(armario -> {
                    armario.setStatus(novoStatus);
                    return armarioRepository.save(armario);
                });
    }

    public Optional<Armario> buscarPorId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do armário não pode ser nulo");
        }
        logger.debug("Buscando armário com ID: {}", id);
        return armarioRepository.findById(id);
    }

    public long contarPorStatus(ArmarioStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status não pode ser nulo");
        }
        logger.debug("Contando armários com status: {}", status);
        return armarioRepository.countByStatus(status);
    }
}
