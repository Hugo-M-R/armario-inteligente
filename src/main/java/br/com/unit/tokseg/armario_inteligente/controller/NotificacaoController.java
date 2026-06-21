package br.com.unit.tokseg.armario_inteligente.controller;

import br.com.unit.tokseg.armario_inteligente.dto.NotificacaoResponse;
import br.com.unit.tokseg.armario_inteligente.model.Notificacao;
import br.com.unit.tokseg.armario_inteligente.service.NotificacaoService;
import br.com.unit.tokseg.armario_inteligente.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MORADOR')")
    public ResponseEntity<List<NotificacaoResponse>> listarTodas() {
        List<NotificacaoResponse> notificacoes = notificacaoService.listarParaUsuario(SecurityUtils.getCurrentUsuario()).stream()
                .map(NotificacaoResponse::from)
                .toList();
        return ResponseEntity.ok(notificacoes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MORADOR')")
    public ResponseEntity<NotificacaoResponse> buscarPorId(@PathVariable String id) {
        return notificacaoService.buscarPorId(id, SecurityUtils.getCurrentUsuario())
                .map(NotificacaoResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/lida")
    @PreAuthorize("hasAnyRole('ADMIN', 'MORADOR')")
    public ResponseEntity<NotificacaoResponse> marcarComoLida(@PathVariable String id) {
        return notificacaoService.marcarComoLida(id, SecurityUtils.getCurrentUsuario())
                .map(NotificacaoResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificacaoResponse> criar(@RequestBody Notificacao notificacao) {
        return ResponseEntity.ok(NotificacaoResponse.from(notificacaoService.salvar(notificacao)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        notificacaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
