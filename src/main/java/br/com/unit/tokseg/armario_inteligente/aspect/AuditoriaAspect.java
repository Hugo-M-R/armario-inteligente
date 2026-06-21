package br.com.unit.tokseg.armario_inteligente.aspect;

import br.com.unit.tokseg.armario_inteligente.annotation.Auditavel;
import br.com.unit.tokseg.armario_inteligente.model.*;
import br.com.unit.tokseg.armario_inteligente.repository.UsuarioRepository;
import br.com.unit.tokseg.armario_inteligente.service.NotificacaoService;
import br.com.unit.tokseg.armario_inteligente.service.RegistroAuditoriaService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Aspect
@Component
public class AuditoriaAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaAspect.class);
    private final RegistroAuditoriaService registroAuditoriaService;
    private final NotificacaoService notificacaoService;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaAspect(
            RegistroAuditoriaService registroAuditoriaService,
            NotificacaoService notificacaoService,
            UsuarioRepository usuarioRepository) {
        this.registroAuditoriaService = registroAuditoriaService;
        this.notificacaoService = notificacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    @Around("@annotation(auditavel)")
    public Object registrarAuditoria(ProceedingJoinPoint joinPoint, Auditavel auditavel) throws Throwable {
        String acao = auditavel.acao().isEmpty() ?
            joinPoint.getSignature().getName() : auditavel.acao();

        String detalhes = auditavel.detalhes().isEmpty() ?
            String.format("Método: %s, Classe: %s",
                joinPoint.getSignature().getName(),
                joinPoint.getTarget().getClass().getSimpleName()) :
            auditavel.detalhes();

        RegistroAuditoria registro = new RegistroAuditoria();
        registro.setAcao(acao);
        registro.setDetalhes(detalhes);
        registro.setDataHora(LocalDateTime.now());

        try {
            Object resultado = joinPoint.proceed();

            registroAuditoriaService.salvar(registro);
            criarNotificacaoSeNecessario(acao, resultado);

            return resultado;
        } catch (Exception e) {
            registro.setDetalhes(registro.getDetalhes() + " - Erro: " + e.getMessage());
            registroAuditoriaService.salvar(registro);
            throw e;
        }
    }

    private void criarNotificacaoSeNecessario(String acao, Object resultado) {
        try {
            if (resultado instanceof Usuario usuario) {
                if (acao.contains("REGISTRO_USUARIO")) {
                    criarNotificacaoAdmin("Novo usuário registrado: " + usuario.getEmail());
                }
            } else if (resultado instanceof Armario armario) {
                if (acao.contains("CADASTRO_ARMARIO")) {
                    criarNotificacaoAdmin("Novo armário cadastrado: " + armario.getNumero());
                }
            } else if (resultado instanceof Encomenda encomenda) {
                if (acao.contains("CADASTRO_ENCOMENDA") && encomenda.getUsuario() != null
                        && encomenda.getArmario() != null) {
                    criarNotificacaoUsuario(encomenda.getUsuario(),
                        "Nova encomenda registrada para você no armário " +
                        encomenda.getArmario().getNumero());
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao criar notificação: {}", e.getMessage(), e);
        }
    }

    private void criarNotificacaoAdmin(String mensagem) {
        usuarioRepository.findByTipo(TipoUsuarioEnum.ADMIN).forEach(admin -> {
            Notificacao notificacao = new Notificacao(
                    UUID.randomUUID().toString(),
                    admin,
                    mensagem,
                    LocalDateTime.now()
            );
            notificacao.setLida(false);
            notificacaoService.salvar(notificacao);
        });
        logger.info("Notificação para admins: {}", mensagem);
    }

    private void criarNotificacaoUsuario(Usuario usuario, String mensagem) {
        Notificacao notificacao = new Notificacao(
            UUID.randomUUID().toString(),
            usuario,
            mensagem,
            LocalDateTime.now()
        );
        notificacao.setLida(false);
        notificacaoService.salvar(notificacao);
    }
}
