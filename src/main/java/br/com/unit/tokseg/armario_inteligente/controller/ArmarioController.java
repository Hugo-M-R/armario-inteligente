package br.com.unit.tokseg.armario_inteligente.controller;

import br.com.unit.tokseg.armario_inteligente.model.Armario;
import br.com.unit.tokseg.armario_inteligente.model.ArmarioStatus;
import br.com.unit.tokseg.armario_inteligente.service.ArmarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/armarios")
public class ArmarioController {

    private final ArmarioService armarioService;

    public ArmarioController(ArmarioService armarioService) {
        this.armarioService = armarioService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Armario> criar(@Valid @RequestBody Armario armario) {
        if (armarioService.existeNumero(armario.getNumero())) {
            return ResponseEntity.badRequest().build();
        }

        Armario novo = armarioService.salvar(armario);
        return ResponseEntity.ok(novo);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Armario>> listarTodos() {
        return ResponseEntity.ok(armarioService.listarTodos());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Armario>> buscarPorStatus(@PathVariable ArmarioStatus status) {
        return ResponseEntity.ok(armarioService.buscarPorStatus(status));
    }

    @GetMapping("/localizacao/{localizacao}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Armario>> buscarPorLocalizacao(@PathVariable String localizacao) {
        return ResponseEntity.ok(armarioService.buscarPorLocalizacao(localizacao));
    }

    @GetMapping("/filtro")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Armario>> buscarPorStatusELocalizacao(
            @RequestParam ArmarioStatus status,
            @RequestParam String localizacao) {
        return ResponseEntity.ok(armarioService.buscarPorStatusELocalizacao(status, localizacao));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Armario> atualizarStatus(
            @PathVariable UUID id,
            @RequestParam ArmarioStatus novoStatus) {
        return armarioService.atualizarStatus(id, novoStatus)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Armario> buscarPorId(@PathVariable UUID id) {
        return armarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/contar/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> contarPorStatus(@PathVariable ArmarioStatus status) {
        return ResponseEntity.ok(armarioService.contarPorStatus(status));
    }
}
