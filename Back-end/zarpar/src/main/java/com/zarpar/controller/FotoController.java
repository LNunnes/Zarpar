package com.zarpar.controller;

import com.zarpar.domain.Foto;
import com.zarpar.service.FotoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/fotos")
public class FotoController {

    private final FotoService service;

    public FotoController(FotoService service) {
        this.service = service;
    }

    @GetMapping("/ponto/{pontoId}")
    public ResponseEntity<List<Foto>> listar(@PathVariable Long pontoId) {
        return ResponseEntity.ok(service.listarPorPonto(pontoId));
    }

    @GetMapping("/arquivo/{filename}")
    public ResponseEntity<Resource> carregarArquivo(@PathVariable String filename, HttpServletRequest request) {
        Resource resource = service.carregarArquivo(filename);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) { }

        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/ponto/{pontoId}")
    public ResponseEntity<?> upload(@PathVariable Long pontoId,
                                    @RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "titulo", required = false) String titulo,
                                    @RequestParam(value = "descricao", required = false) String descricao,
                                    HttpSession session) {

        Object userId = session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login necessário");

        try {
            Foto foto = service.salvar(pontoId, (Long) userId, file, titulo, descricao);
            return ResponseEntity.status(HttpStatus.CREATED).body(foto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable String id, HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login necessário");

        service.excluir(id, (Long) userId);
        return ResponseEntity.noContent().build();
    }
}