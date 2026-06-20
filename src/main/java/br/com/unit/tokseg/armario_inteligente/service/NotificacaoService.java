package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.model.Notificacao;
import br.com.unit.tokseg.armario_inteligente.repository.NotificacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);
    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    @Transactional
    public Notificacao salvar(Notificacao notificacao) {
        if (notificacao == null) {
            throw new IllegalArgumentException("Notificação não pode ser nula");
        }
        if (notificacao.getMensagem() == null || notificacao.getMensagem().trim().isEmpty()) {
            throw new IllegalArgumentException("Mensagem da notificação não pode ser nula ou vazia");
        }
        if (notificacao.getUsuario() == null) {
            throw new IllegalArgumentException("Usuário da notificação não pode ser nulo");
        }

        logger.info("Salvando nova notificação para o usuário: {}", notificacao.getUsuario().getId());
        return notificacaoRepository.save(notificacao);
    }

    public List<Notificacao> listarTodas() {
        logger.debug("Listando todas as notificações");
        return notificacaoRepository.findAll();
    }

    public Optional<Notificacao> buscarPorId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID da notificação não pode ser nulo");
        }
        logger.debug("Buscando notificação com ID: {}", id);
        return notificacaoRepository.findById(id);
    }

    @Transactional
    public Optional<Notificacao> marcarComoLida(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID da notificação não pode ser nulo");
        }

        logger.info("Marcando notificação como lida: {}", id);
        return notificacaoRepository.findById(id)
                .map(notificacao -> {
                    notificacao.setLida(true);
                    return notificacaoRepository.save(notificacao);
                });
    }

    @Transactional
    public void remover(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID da notificação não pode ser nulo");
        }
        
        if (!notificacaoRepository.existsById(id)) {
            throw new EntityNotFoundException("Notificação não encontrada com ID: " + id);
        }

        logger.info("Removendo notificação com ID: {}", id);
        notificacaoRepository.deleteById(id);
    }
}
