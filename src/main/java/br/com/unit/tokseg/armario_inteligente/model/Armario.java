package br.com.unit.tokseg.armario_inteligente.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "armario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Armario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArmarioStatus status;

    @Column(nullable = false)
    private String localizacao;

    @ManyToOne
    @JoinColumn(name = "id_encomenda_atual", referencedColumnName = "id_encomenda")
    private Encomenda encomendaAtual;

    public boolean isOcupado() {
        return status == ArmarioStatus.OCUPADO;
    }

    public UUID getId() { return id; }
    public String getNumero() { return numero; }
    public ArmarioStatus getStatus() { return status; }
    public String getLocalizacao() { return localizacao; }
    public Encomenda getEncomendaAtual() { return encomendaAtual; }

    public void setId(UUID id) { this.id = id; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setStatus(ArmarioStatus status) { this.status = status; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public void setEncomendaAtual(Encomenda encomendaAtual) { this.encomendaAtual = encomendaAtual; }
}
