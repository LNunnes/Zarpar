package com.zarpar.dto;

import com.zarpar.domain.Hospedagem;
import com.zarpar.domain.TipoHospedagem;
import java.math.BigDecimal;

public class HospedagemResponse {

    private Long id;
    private Long pontoId;
    private String nome;
    private String endereco;
    private String telefone;
    private BigDecimal precoMedio;
    private TipoHospedagem tipo;
    private String linkReserva;
    private Long criadoPorId;
    private String criadoPorNome;

    public HospedagemResponse() {}

    public HospedagemResponse(Hospedagem h) {
        this.id = h.getId();
        this.pontoId = h.getPonto().getId();
        this.nome = h.getNome();
        this.endereco = h.getEndereco();
        this.telefone = h.getTelefone();
        this.precoMedio = h.getPrecoMedio();
        this.tipo = h.getTipo();
        this.linkReserva = h.getLinkReserva();
        this.criadoPorId = h.getCriadoPor().getId();
        this.criadoPorNome = h.getCriadoPor().getNome();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPontoId() { return pontoId; }
    public void setPontoId(Long pontoId) { this.pontoId = pontoId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public BigDecimal getPrecoMedio() { return precoMedio; }
    public void setPrecoMedio(BigDecimal precoMedio) { this.precoMedio = precoMedio; }

    public TipoHospedagem getTipo() { return tipo; }
    public void setTipo(TipoHospedagem tipo) { this.tipo = tipo; }

    public String getLinkReserva() { return linkReserva; }
    public void setLinkReserva(String linkReserva) { this.linkReserva = linkReserva; }

    public Long getCriadoPorId() { return criadoPorId; }
    public void setCriadoPorId(Long criadoPorId) { this.criadoPorId = criadoPorId; }

    public String getCriadoPorNome() { return criadoPorNome; }
    public void setCriadoPorNome(String criadoPorNome) { this.criadoPorNome = criadoPorNome; }
}

