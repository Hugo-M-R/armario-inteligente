package br.com.unit.tokseg.armario_inteligente.controller;

import br.com.unit.tokseg.armario_inteligente.model.RegistroAuditoria;
import br.com.unit.tokseg.armario_inteligente.service.RegistroAuditoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class RegistroAuditoriaController {

    private final RegistroAuditoriaService registroAuditoriaService;

    public RegistroAuditoriaController(RegistroAuditoriaService registroAuditoriaService) {
        this.registroAuditoriaService = registroAuditoriaService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroAuditoria>> listarTodos() {
        return ResponseEntity.ok(registroAuditoriaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroAuditoria> buscarPorId(@PathVariable Integer id) {
        return registroAuditoriaService.buscarPorId(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
