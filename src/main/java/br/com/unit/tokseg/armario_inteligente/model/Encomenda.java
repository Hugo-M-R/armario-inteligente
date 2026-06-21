package br.com.unit.tokseg.armario_inteligente.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Encomenda")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Encomenda {

    @Id
    @Column(name = "id_encomenda")
    private String idEncomenda;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String remetente;

    @Column(name = "dataRecebimento", nullable = false)
    private LocalDateTime dataRecebimento;

    @ManyToOne
    @JoinColumn(name = "armario_id", referencedColumnName = "id")
    private Armario armario;

    @ManyToOne
    @JoinColumn(name = "idUsuario", referencedColumnName = "id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_retirada", nullable = false)
    private StatusRetirada statusRetirada = StatusRetirada.PENDENTE;

    @Column(name = "data_retirada")
    private LocalDateTime dataRetirada;

    @Column(name = "codigo_acesso")
    private String codigoAcesso;

    @Column(name = "data_expiracao_codigo")
    private LocalDateTime dataExpiracaoCodigo;

    public String getIdEncomenda() {
        return idEncomenda;
    }

    public void setIdEncomenda(String idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public LocalDateTime getDataRecebimento() {
        return dataRecebimento;
    }

    public void setDataRecebimento(LocalDateTime dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }

    public Armario getArmario() {
        return armario;
    }

    public void setArmario(Armario armario) {
        this.armario = armario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public StatusRetirada getStatusRetirada() {
        return statusRetirada;
    }

    public void setStatusRetirada(StatusRetirada statusRetirada) {
        this.statusRetirada = statusRetirada;
    }

    public LocalDateTime getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDateTime dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    public void setCodigoAcesso(String codigoAcesso) {
        this.codigoAcesso = codigoAcesso;
    }

    public LocalDateTime getDataExpiracaoCodigo() {
        return dataExpiracaoCodigo;
    }

    public void setDataExpiracaoCodigo(LocalDateTime dataExpiracaoCodigo) {
        this.dataExpiracaoCodigo = dataExpiracaoCodigo;
    }
}
