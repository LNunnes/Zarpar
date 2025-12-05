package com.zarpar.dto;

import com.zarpar.domain.Categoria;
import jakarta.validation.constraints.NotBlank;
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

    private BigDecimal latitude;
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