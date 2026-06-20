package br.com.unit.tokseg.armario_inteligente.controller;

import br.com.unit.tokseg.armario_inteligente.model.Compartimento;
import br.com.unit.tokseg.armario_inteligente.service.CompartimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/compartimentos")
public class CompartimentoController {

    private final CompartimentoService compartimentoService;

    public CompartimentoController(CompartimentoService compartimentoService) {
        this.compartimentoService = compartimentoService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Compartimento>> listarTodos() {
        return ResponseEntity.ok(compartimentoService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Compartimento> buscarPorId(@PathVariable UUID id) {
        return compartimentoService.buscarPorId(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Compartimento> criar(@RequestBody Compartimento compartimento) {
        return ResponseEntity.ok(compartimentoService.salvar(compartimento));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        compartimentoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
