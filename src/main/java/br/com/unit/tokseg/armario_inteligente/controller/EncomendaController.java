package br.com.unit.tokseg.armario_inteligente.controller;

import br.com.unit.tokseg.armario_inteligente.dto.CodigoAcessoResponse;
import br.com.unit.tokseg.armario_inteligente.dto.EncomendaRequest;
import br.com.unit.tokseg.armario_inteligente.dto.EncomendaResponse;
import br.com.unit.tokseg.armario_inteligente.dto.ValidarCodigoRequest;
import br.com.unit.tokseg.armario_inteligente.model.Encomenda;
import br.com.unit.tokseg.armario_inteligente.service.EncomendaService;
import br.com.unit.tokseg.armario_inteligente.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/encomendas")
public class EncomendaController {

    private final EncomendaService encomendaService;

    public EncomendaController(EncomendaService encomendaService) {
        this.encomendaService = encomendaService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EncomendaResponse>> listarTodas() {
        List<EncomendaResponse> encomendas = encomendaService.listarParaUsuario(SecurityUtils.getCurrentUsuario()).stream()
                .map(EncomendaResponse::from)
                .toList();
        return ResponseEntity.ok(encomendas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EncomendaResponse> buscarPorId(@PathVariable String id) {
        return encomendaService.buscarPorId(id, SecurityUtils.getCurrentUsuario())
                .map(EncomendaResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PORTEIRO')")
    public ResponseEntity<EncomendaResponse> criar(@Valid @RequestBody EncomendaRequest request) {
        Encomenda criada = encomendaService.criar(request);
        return ResponseEntity.ok(EncomendaResponse.from(criada));
    }

    @PostMapping("/{id}/gerar-codigo")
    @PreAuthorize("hasAnyRole('ADMIN', 'PORTEIRO')")
    public ResponseEntity<CodigoAcessoResponse> gerarCodigo(@PathVariable String id) {
        return ResponseEntity.ok(encomendaService.gerarCodigo(id, SecurityUtils.getCurrentUsuario()));
    }

    @PostMapping("/{id}/validar-codigo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> validarCodigo(
            @PathVariable String id,
            @Valid @RequestBody ValidarCodigoRequest request) {
        encomendaService.validarCodigo(id, request.codigo(), SecurityUtils.getCurrentUsuario());
        return ResponseEntity.ok(Map.of("valido", true));
    }

    @PostMapping("/{id}/retirar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EncomendaResponse> retirar(
            @PathVariable String id,
            @RequestBody(required = false) ValidarCodigoRequest request) {
        String codigo = request != null ? request.codigo() : null;
        Encomenda encomenda = encomendaService.retirar(id, codigo, SecurityUtils.getCurrentUsuario());
        return ResponseEntity.ok(EncomendaResponse.from(encomenda));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        encomendaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
