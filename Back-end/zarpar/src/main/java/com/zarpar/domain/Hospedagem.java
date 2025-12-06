package com.zarpar.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hospedagens")
public class Hospedagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ponto_id", nullable = false)
    private PontoTuristico ponto;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String endereco;

    private String telefone;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoMedio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHospedagem tipo;

    private String linkReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por_id", nullable = false)
    private Usuario criadoPor;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Hospedagem() {}

    public Hospedagem(PontoTuristico ponto, String nome, String endereco, String telefone, 
                      BigDecimal precoMedio, TipoHospedagem tipo, String linkReserva, Usuario criadoPor) {
        this.ponto = ponto;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.precoMedio = precoMedio;
        this.tipo = tipo;
        this.linkReserva = linkReserva;
        this.criadoPor = criadoPor;
        this.createdAt = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PontoTuristico getPonto() { return ponto; }
    public void setPonto(PontoTuristico ponto) { this.ponto = ponto; }

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

    public Usuario getCriadoPor() { return criadoPor; }
    public void setCriadoPor(Usuario criadoPor) { this.criadoPor = criadoPor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

