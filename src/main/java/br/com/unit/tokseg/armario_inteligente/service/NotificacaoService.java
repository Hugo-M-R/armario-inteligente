package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.exception.ForbiddenOperationException;
import br.com.unit.tokseg.armario_inteligente.model.Notificacao;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import br.com.unit.tokseg.armario_inteligente.repository.NotificacaoRepository;
import br.com.unit.tokseg.armario_inteligente.util.SecurityUtils;
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

    public List<Notificacao> listarParaUsuario(Usuario requester) {
        if (SecurityUtils.isPorteiro(requester)) {
            throw new ForbiddenOperationException("Porteiros não têm acesso a notificações");
        }
        if (SecurityUtils.isAdmin(requester)) {
            logger.debug("Listando todas as notificações");
            return notificacaoRepository.findAll();
        }
        logger.debug("Listando notificações do usuário: {}", requester.getId());
        return notificacaoRepository.findByUsuarioId(requester.getId());
    }

    public Optional<Notificacao> buscarPorId(String id, Usuario requester) {
        if (id == null) {
            throw new IllegalArgumentException("ID da notificação não pode ser nulo");
        }
        if (SecurityUtils.isPorteiro(requester)) {
            throw new ForbiddenOperationException("Porteiros não têm acesso a notificações");
        }
        logger.debug("Buscando notificação com ID: {}", id);
        Optional<Notificacao> notificacao = notificacaoRepository.findById(id);
        if (notificacao.isEmpty()) {
            return Optional.empty();
        }
        if (SecurityUtils.isMorador(requester)
                && (notificacao.get().getUsuario() == null
                || !notificacao.get().getUsuario().getId().equals(requester.getId()))) {
            return Optional.empty();
        }
        return notificacao;
    }

    @Transactional
    public Optional<Notificacao> marcarComoLida(String id, Usuario requester) {
        Optional<Notificacao> notificacao = buscarPorId(id, requester);
        if (notificacao.isEmpty()) {
            return Optional.empty();
        }
        logger.info("Marcando notificação como lida: {}", id);
        Notificacao atualizada = notificacao.get();
        atualizada.setLida(true);
        return Optional.of(notificacaoRepository.save(atualizada));
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
