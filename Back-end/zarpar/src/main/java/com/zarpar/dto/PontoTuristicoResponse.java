package com.zarpar.dto;

import com.zarpar.domain.Categoria;
import com.zarpar.domain.PontoTuristico;
import java.math.BigDecimal;

public class PontoTuristicoResponse {

    private Long id;
    private String nome;
    private String descricao;
    private String cidade;
    private String estado;
    private String pais;
    private String endereco;
    private String comoChegar;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long criadoPorId;
    private String criadoPorNome;
    private Categoria categoria;
    private Double mediaAvaliacoes;
    private String capaFilename;

    public PontoTuristicoResponse(PontoTuristico p) {
        this.id = p.getId();
        this.nome = p.getNome();
        this.descricao = p.getDescricao();
        this.cidade = p.getCidade();
        this.estado = p.getEstado();
        this.pais = p.getPais();
        this.endereco = p.getEndereco();
        this.comoChegar = p.getComoChegar();
        this.latitude = p.getLatitude();
        this.longitude = p.getLongitude();
        if (p.getCriadoPor() != null) {
            this.criadoPorId = p.getCriadoPor().getId();
            this.criadoPorNome = p.getCriadoPor().getNome();
        }
        this.categoria = p.getCategoria();
        this.mediaAvaliacoes = p.getMediaAvaliacoes();
        this.capaFilename = p.getCapaFilename();
    }

    // Apenas Getters são necessários para serialização JSON
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getPais() { return pais; }
    public String getEndereco() { return endereco; }
    public String getComoChegar() { return comoChegar; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public Long getCriadoPorId() { return criadoPorId; }
    public String getCriadoPorNome() { return criadoPorNome; }
    public Categoria getCategoria() { return categoria; }
    public Double getMediaAvaliacoes() { return mediaAvaliacoes; }
    public String getCapaFilename() { return capaFilename; }
}