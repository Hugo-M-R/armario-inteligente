package br.com.unit.tokseg.armario_inteligente.controller;

import br.com.unit.tokseg.armario_inteligente.dto.NotificacaoResponse;
import br.com.unit.tokseg.armario_inteligente.dto.UsuarioAtivoRequest;
import br.com.unit.tokseg.armario_inteligente.dto.UsuarioCreateRequest;
import br.com.unit.tokseg.armario_inteligente.dto.UsuarioResponse;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import br.com.unit.tokseg.armario_inteligente.service.UsuarioService;
import br.com.unit.tokseg.armario_inteligente.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PORTEIRO')")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioCreateRequest request) {
        Usuario requester = SecurityUtils.getCurrentUsuario();
        Usuario criado = usuarioService.criar(request, requester);
        return ResponseEntity.ok(UsuarioResponse.from(criado));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PORTEIRO')")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> usuarios = usuarioService.listarTodos().stream()
                .map(UsuarioResponse::from)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        Usuario requester = SecurityUtils.getCurrentUsuario();
        return usuarioService.buscarPorIdComOwnership(id, requester)
                .map(UsuarioResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/ativo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> atualizarAtivo(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioAtivoRequest request) {
        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.atualizarAtivo(id, request.ativo())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        try {
            usuarioService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
