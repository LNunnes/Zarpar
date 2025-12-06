package com.zarpar.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "avaliacoes")
public class Avaliacao {

    @Id
    private String id;

    private Long pontoId;
    private Long usuarioId;
    private String nomeUsuario;

    private Integer nota;
    private String comentario;

    private LocalDateTime data = LocalDateTime.now();

    public Avaliacao() {}

    public Avaliacao(Long pontoId, Long usuarioId, String nomeUsuario, Integer nota, String comentario) {
        this.pontoId = pontoId;
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
        this.nota = nota;
        this.comentario = comentario;
        this.data = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getPontoId() { return pontoId; }
    public void setPontoId(Long pontoId) { this.pontoId = pontoId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getData() { return data; }
}