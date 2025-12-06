package com.zarpar.dto;

import com.zarpar.domain.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class PontoTuristicoRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;

    private String estado;
    private String pais;
    private String endereco;

    private String comoChegar;

    @DecimalMin(value = "-90", message = "Latitude deve estar entre -90 e +90")
    @DecimalMax(value = "90", message = "Latitude deve estar entre -90 e +90")
    private BigDecimal latitude;
    
    @DecimalMin(value = "-180", message = "Longitude deve estar entre -180 e +180")
    @DecimalMax(value = "180", message = "Longitude deve estar entre -180 e +180")
    private BigDecimal longitude;
    
    private Categoria categoria;

    public PontoTuristicoRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getComoChegar() { return comoChegar; }
    public void setComoChegar(String comoChegar) { this.comoChegar = comoChegar; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}