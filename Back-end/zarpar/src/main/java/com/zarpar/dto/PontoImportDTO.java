package com.zarpar.dto;

import com.zarpar.domain.Categoria;
import java.math.BigDecimal;

public class PontoImportDTO {
    private String nome;
    private String descricao;
    private String cidade;
    private String estado;
    private String pais;
    private String endereco;
    private String comoChegar;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String categoria; // Recebe como string para facilitar parse

    public PontoImportDTO() {}

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getComoChegar() {
        return comoChegar;
    }

    public void setComoChegar(String comoChegar) {
        this.comoChegar = comoChegar;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // Método auxiliar para converter categoria string para enum
    public Categoria getCategoriaEnum() {
        if (categoria == null || categoria.trim().isEmpty()) {
            return null;
        }
        try {
            return Categoria.valueOf(categoria.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

