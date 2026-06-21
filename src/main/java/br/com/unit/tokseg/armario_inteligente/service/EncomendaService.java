package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.annotation.Auditavel;
import br.com.unit.tokseg.armario_inteligente.dto.CodigoAcessoResponse;
import br.com.unit.tokseg.armario_inteligente.dto.EncomendaRequest;
import br.com.unit.tokseg.armario_inteligente.exception.ForbiddenOperationException;
import br.com.unit.tokseg.armario_inteligente.model.Armario;
import br.com.unit.tokseg.armario_inteligente.model.ArmarioStatus;
import br.com.unit.tokseg.armario_inteligente.model.Compartimento;
import br.com.unit.tokseg.armario_inteligente.model.Encomenda;
import br.com.unit.tokseg.armario_inteligente.model.StatusRetirada;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import br.com.unit.tokseg.armario_inteligente.repository.ArmarioRepository;
import br.com.unit.tokseg.armario_inteligente.repository.CompartimentoRepository;
import br.com.unit.tokseg.armario_inteligente.repository.EncomendaRepository;
import br.com.unit.tokseg.armario_inteligente.repository.UsuarioRepository;
import br.com.unit.tokseg.armario_inteligente.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EncomendaService {

    private static final Logger logger = LoggerFactory.getLogger(EncomendaService.class);
    private static final int CODIGO_VALIDADE_HORAS = 24;

    private final EncomendaRepository encomendaRepository;
    private final ArmarioRepository armarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CompartimentoRepository compartimentoRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public EncomendaService(
            EncomendaRepository encomendaRepository,
            ArmarioRepository armarioRepository,
            UsuarioRepository usuarioRepository,
            CompartimentoRepository compartimentoRepository) {
        this.encomendaRepository = encomendaRepository;
        this.armarioRepository = armarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.compartimentoRepository = compartimentoRepository;
    }

    @Auditavel(acao = "CADASTRO_ENCOMENDA", detalhes = "Cadastro de nova encomenda no sistema")
    @Transactional
    public Encomenda criar(EncomendaRequest request) {
        Armario armario = armarioRepository.findById(request.armarioId())
                .orElseThrow(() -> new IllegalArgumentException("Armário não encontrado"));
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Encomenda encomenda = Encomenda.builder()
                .idEncomenda(request.idEncomenda())
                .descricao(request.descricao())
                .remetente(request.remetente())
                .dataRecebimento(LocalDateTime.now())
                .armario(armario)
                .usuario(usuario)
                .statusRetirada(StatusRetirada.PENDENTE)
                .build();

        armario.setStatus(ArmarioStatus.OCUPADO);
        armario.setEncomendaAtual(encomenda);
        armarioRepository.save(armario);

        logger.info("Salvando nova encomenda com ID: {}", encomenda.getIdEncomenda());
        return encomendaRepository.save(encomenda);
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
        if (encomenda.getDataRecebimento() == null) {
            encomenda.setDataRecebimento(LocalDateTime.now());
        }
        if (encomenda.getStatusRetirada() == null) {
            encomenda.setStatusRetirada(StatusRetirada.PENDENTE);
        }

        logger.info("Salvando nova encomenda com ID: {}", encomenda.getIdEncomenda());
        return encomendaRepository.save(encomenda);
    }

    public List<Encomenda> listarParaUsuario(Usuario requester) {
        if (SecurityUtils.canManageAllEncomendas(requester)) {
            logger.debug("Listando todas as encomendas");
            return encomendaRepository.findAll();
        }
        logger.debug("Listando encomendas do usuário: {}", requester.getId());
        return encomendaRepository.findByUsuarioId(requester.getId());
    }

    public Optional<Encomenda> buscarPorId(String id, Usuario requester) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da encomenda não pode ser nulo ou vazio");
        }
        logger.debug("Buscando encomenda com ID: {}", id);
        Optional<Encomenda> encomenda = encomendaRepository.findById(id);
        if (encomenda.isEmpty()) {
            return Optional.empty();
        }
        if (SecurityUtils.isMorador(requester)
                && (encomenda.get().getUsuario() == null
                || !encomenda.get().getUsuario().getId().equals(requester.getId()))) {
            return Optional.empty();
        }
        return encomenda;
    }

    @Auditavel(acao = "GERAR_CODIGO_ENCOMENDA", detalhes = "Geração de código de acesso para retirada")
    @Transactional
    public CodigoAcessoResponse gerarCodigo(String id, Usuario requester) {
        if (!SecurityUtils.canManageAllEncomendas(requester)) {
            throw new ForbiddenOperationException("Apenas administradores e porteiros podem gerar códigos");
        }

        Encomenda encomenda = buscarEncomendaPendente(id);
        String codigo = String.format("%06d", secureRandom.nextInt(1_000_000));
        LocalDateTime expiracao = LocalDateTime.now().plusHours(CODIGO_VALIDADE_HORAS);

        encomenda.setCodigoAcesso(codigo);
        encomenda.setDataExpiracaoCodigo(expiracao);
        encomendaRepository.save(encomenda);

        return new CodigoAcessoResponse(codigo, expiracao);
    }

    @Transactional(readOnly = true)
    public boolean validarCodigo(String id, String codigo, Usuario requester) {
        Encomenda encomenda = buscarEncomendaComOwnership(id, requester);
        validarCodigoInterno(encomenda, codigo);
        return true;
    }

    @Auditavel(acao = "RETIRADA_ENCOMENDA", detalhes = "Retirada de encomenda do armário")
    @Transactional
    public Encomenda retirar(String id, String codigo, Usuario requester) {
        Encomenda encomenda = buscarEncomendaComOwnership(id, requester);

        if (SecurityUtils.isMorador(requester)) {
            if (codigo == null || codigo.isBlank()) {
                throw new IllegalArgumentException("Código de acesso é obrigatório para retirada");
            }
            validarCodigoInterno(encomenda, codigo);
        }

        if (encomenda.getStatusRetirada() == StatusRetirada.RETIRADA) {
            throw new IllegalArgumentException("Encomenda já foi retirada");
        }

        encomenda.setStatusRetirada(StatusRetirada.RETIRADA);
        encomenda.setDataRetirada(LocalDateTime.now());
        encomenda.setCodigoAcesso(null);
        encomenda.setDataExpiracaoCodigo(null);

        liberarRecursos(encomenda);
        return encomendaRepository.save(encomenda);
    }

    @Auditavel(acao = "RETIRADA_ENCOMENDA", detalhes = "Remoção administrativa de encomenda")
    @Transactional
    public void remover(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da encomenda não pode ser nulo ou vazio");
        }

        Encomenda encomenda = encomendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada com ID: " + id));

        liberarRecursos(encomenda);
        logger.info("Removendo encomenda com ID: {}", id);
        encomendaRepository.deleteById(id);
    }

    private Encomenda buscarEncomendaPendente(String id) {
        Encomenda encomenda = encomendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada com ID: " + id));
        if (encomenda.getStatusRetirada() == StatusRetirada.RETIRADA) {
            throw new IllegalArgumentException("Encomenda já foi retirada");
        }
        return encomenda;
    }

    private Encomenda buscarEncomendaComOwnership(String id, Usuario requester) {
        return buscarPorId(id, requester)
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada com ID: " + id));
    }

    private void validarCodigoInterno(Encomenda encomenda, String codigo) {
        if (encomenda.getStatusRetirada() == StatusRetirada.RETIRADA) {
            throw new IllegalArgumentException("Encomenda já foi retirada");
        }
        if (encomenda.getCodigoAcesso() == null || encomenda.getDataExpiracaoCodigo() == null) {
            throw new IllegalArgumentException("Código de acesso não foi gerado para esta encomenda");
        }
        if (LocalDateTime.now().isAfter(encomenda.getDataExpiracaoCodigo())) {
            throw new IllegalArgumentException("Código de acesso expirado");
        }
        if (!encomenda.getCodigoAcesso().equals(codigo)) {
            throw new IllegalArgumentException("Código de acesso inválido");
        }
    }

    private void liberarRecursos(Encomenda encomenda) {
        if (encomenda.getArmario() != null) {
            Armario armario = encomenda.getArmario();
            armario.setStatus(ArmarioStatus.DISPONIVEL);
            armario.setEncomendaAtual(null);
            armarioRepository.save(armario);
        }

        compartimentoRepository.findByEncomendaAtualIdEncomenda(encomenda.getIdEncomenda())
                .forEach(compartimento -> {
                    compartimento.setOcupado(false);
                    compartimento.setEncomendaAtual(null);
                    compartimentoRepository.save(compartimento);
                });
    }
}
