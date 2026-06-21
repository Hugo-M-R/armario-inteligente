package br.com.unit.tokseg.armario_inteligente.repository;

import br.com.unit.tokseg.armario_inteligente.model.Encomenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EncomendaRepository extends JpaRepository<Encomenda, String> {
    List<Encomenda> findByUsuarioId(UUID usuarioId);
} 