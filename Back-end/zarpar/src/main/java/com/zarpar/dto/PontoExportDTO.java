package com.zarpar.dto;

import java.math.BigDecimal;
import java.util.List;

public class PontoExportDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String cidade;
    private String estado;
    private String pais;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String endereco;
    private String categoria;
    private String comoChegar;
    private Double mediaAvaliacoes;
    private Integer quantidadeAvaliacoes;
    private List<HospedagemExportDTO> hospedagens;

    public PontoExportDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getComoChegar() { return comoChegar; }
    public void setComoChegar(String comoChegar) { this.comoChegar = comoChegar; }

    public Double getMediaAvaliacoes() { return mediaAvaliacoes; }
    public void setMediaAvaliacoes(Double mediaAvaliacoes) { this.mediaAvaliacoes = mediaAvaliacoes; }

    public Integer getQuantidadeAvaliacoes() { return quantidadeAvaliacoes; }
    public void setQuantidadeAvaliacoes(Integer quantidadeAvaliacoes) { this.quantidadeAvaliacoes = quantidadeAvaliacoes; }

    public List<HospedagemExportDTO> getHospedagens() { return hospedagens; }
    public void setHospedagens(List<HospedagemExportDTO> hospedagens) { this.hospedagens = hospedagens; }

    public static class HospedagemExportDTO {
        private String nome;
        private String tipo;
        private BigDecimal precoMedio;
        private String endereco;
        private String telefone;
        private String linkReserva;

        public HospedagemExportDTO() {}

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public BigDecimal getPrecoMedio() { return precoMedio; }
        public void setPrecoMedio(BigDecimal precoMedio) { this.precoMedio = precoMedio; }

        public String getEndereco() { return endereco; }
        public void setEndereco(String endereco) { this.endereco = endereco; }

        public String getTelefone() { return telefone; }
        public void setTelefone(String telefone) { this.telefone = telefone; }

        public String getLinkReserva() { return linkReserva; }
        public void setLinkReserva(String linkReserva) { this.linkReserva = linkReserva; }
    }
}


