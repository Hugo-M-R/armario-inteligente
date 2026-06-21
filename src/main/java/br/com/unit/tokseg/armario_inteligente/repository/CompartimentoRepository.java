package br.com.unit.tokseg.armario_inteligente.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.unit.tokseg.armario_inteligente.model.Compartimento;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompartimentoRepository extends JpaRepository<Compartimento, UUID> {
    List<Compartimento> findByEncomendaAtualIdEncomenda(String idEncomenda);
    Optional<Compartimento> findFirstByArmarioId(UUID armarioId);
}
