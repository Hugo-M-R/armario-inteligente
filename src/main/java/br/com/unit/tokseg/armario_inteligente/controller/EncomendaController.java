package br.com.unit.tokseg.armario_inteligente.controller;

import br.com.unit.tokseg.armario_inteligente.model.Encomenda;
import br.com.unit.tokseg.armario_inteligente.service.EncomendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encomendas")
public class EncomendaController {

    private final EncomendaService encomendaService;

    public EncomendaController(EncomendaService encomendaService) {
        this.encomendaService = encomendaService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Encomenda>> listarTodas() {
        return ResponseEntity.ok(encomendaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Encomenda> buscarPorId(@PathVariable String id) {
        return encomendaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Encomenda> criar(@RequestBody Encomenda encomenda) {
        return ResponseEntity.ok(encomendaService.salvar(encomenda));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        encomendaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
