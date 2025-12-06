package com.zarpar.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "fotos")
public class Foto {

    @Id
    private String id;

    private Long pontoId;
    private Long usuarioId;
    private String nomeUsuario;

    private String filename;
    private String titulo;
    private String descricao;
    private String contentType;

    private LocalDateTime data = LocalDateTime.now();

    public Foto() {}

    public Foto(Long pontoId, Long usuarioId, String nomeUsuario, String filename, String titulo, String descricao, String contentType) {
        this.pontoId = pontoId;
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
        this.filename = filename;
        this.titulo = titulo;
        this.descricao = descricao;
        this.contentType = contentType;
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
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public LocalDateTime getData() { return data; }
}