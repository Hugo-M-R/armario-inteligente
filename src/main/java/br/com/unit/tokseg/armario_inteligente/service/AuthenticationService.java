package br.com.unit.tokseg.armario_inteligente.service;

import br.com.unit.tokseg.armario_inteligente.dto.AuthenticationRequest;
import br.com.unit.tokseg.armario_inteligente.dto.AuthenticationResponse;
import br.com.unit.tokseg.armario_inteligente.dto.RegisterRequest;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import br.com.unit.tokseg.armario_inteligente.repository.UsuarioRepository;
import br.com.unit.tokseg.armario_inteligente.annotation.Auditavel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Auditavel(acao = "REGISTRO_USUARIO", detalhes = "Registro de novo usuário no sistema")
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        logger.info("Iniciando registro de novo usuário: {}", request.email());

        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            logger.warn("Tentativa de registro com email já existente: {}", request.email());
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setTelefone(request.telefone());
        usuario.setTipo(request.tipo() != null ? request.tipo() : TipoUsuarioEnum.MORADOR);

        usuarioRepository.save(usuario);
        logger.info("Usuário registrado com sucesso: {}", usuario.getEmail());

        String jwtToken = jwtService.generateToken(usuario);
        return new AuthenticationResponse(jwtToken);
    }

    @Auditavel(acao = "LOGIN_USUARIO", detalhes = "Login de usuário no sistema")
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        logger.info("Iniciando autenticação para usuário: {}", request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.senha()
                    )
            );

            Usuario usuario = usuarioRepository.findByEmail(request.email())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            String jwtToken = jwtService.generateToken(usuario);
            logger.info("Usuário autenticado com sucesso: {}", usuario.getEmail());

            return new AuthenticationResponse(jwtToken);

        } catch (AuthenticationException e) {
            logger.error("Falha na autenticação do usuário: {}", request.email(), e);
            throw new IllegalArgumentException("Credenciais inválidas");
        }
    }
}
