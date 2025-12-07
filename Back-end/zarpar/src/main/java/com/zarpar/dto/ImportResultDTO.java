package com.zarpar.dto;

import java.util.ArrayList;
import java.util.List;

public class ImportResultDTO {
    private int totalProcessados;
    private int sucessos;
    private int erros;
    private List<String> mensagensErro;

    public ImportResultDTO() {
        this.totalProcessados = 0;
        this.sucessos = 0;
        this.erros = 0;
        this.mensagensErro = new ArrayList<>();
    }

    public void incrementarTotal() {
        this.totalProcessados++;
    }

    public void incrementarSucesso() {
        this.sucessos++;
    }

    public void adicionarErro(String mensagem) {
        this.erros++;
        this.mensagensErro.add(mensagem);
    }

    // Getters
    public int getTotalProcessados() {
        return totalProcessados;
    }

    public int getSucessos() {
        return sucessos;
    }

    public int getErros() {
        return erros;
    }

    public List<String> getMensagensErro() {
        return mensagensErro;
    }
}

