package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.dto.UsuarioCreateRequest;
import br.com.unit.tokseg.armario_inteligente.exception.ForbiddenOperationException;
import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import br.com.unit.tokseg.armario_inteligente.repository.UsuarioRepository;
import br.com.unit.tokseg.armario_inteligente.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional
    public Usuario criar(UsuarioCreateRequest request, Usuario requester) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        TipoUsuarioEnum tipo = resolveTipoForCreation(request.tipo(), requester);

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setTelefone(request.telefone());
        usuario.setTipo(tipo);
        usuario.setAtivo(true);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario salvarComSenhaCodificada(Usuario usuario) {
        if (usuario.getSenha() != null && !usuario.getSenha().startsWith("$2")) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorIdComOwnership(UUID id, Usuario requester) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            return Optional.empty();
        }
        if (SecurityUtils.isMorador(requester) && !usuario.get().getId().equals(requester.getId())) {
            return Optional.empty();
        }
        return usuario;
    }

    public List<Usuario> listarAdmins() {
        return usuarioRepository.findByTipo(TipoUsuarioEnum.ADMIN);
    }

    @Transactional
    public Usuario atualizarAtivo(UUID id, boolean ativo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
        usuario.setAtivo(ativo);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void remover(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private TipoUsuarioEnum resolveTipoForCreation(TipoUsuarioEnum requestedTipo, Usuario requester) {
        if (SecurityUtils.isAdmin(requester)) {
            return requestedTipo;
        }
        if (SecurityUtils.isPorteiro(requester)) {
            if (requestedTipo != TipoUsuarioEnum.MORADOR) {
                throw new ForbiddenOperationException("Porteiros só podem criar usuários do tipo MORADOR");
            }
            return TipoUsuarioEnum.MORADOR;
        }
        throw new ForbiddenOperationException("Operação não permitida");
    }
}
