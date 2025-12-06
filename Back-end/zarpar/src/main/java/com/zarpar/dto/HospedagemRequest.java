package com.zarpar.dto;

import com.zarpar.domain.TipoHospedagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class HospedagemRequest {

    @NotBlank(message = "Nome da hospedagem é obrigatório")
    private String nome;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    private String telefone;

    @NotNull(message = "Preço médio é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço médio deve ser maior que zero")
    private BigDecimal precoMedio;

    @NotNull(message = "Tipo é obrigatório")
    private TipoHospedagem tipo;

    @NotBlank(message = "Link para reserva é obrigatório")
    private String linkReserva;

    public HospedagemRequest() {}

    // Getters e Setters
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
}

