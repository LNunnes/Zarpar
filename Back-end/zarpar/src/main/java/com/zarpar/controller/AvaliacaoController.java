package com.zarpar.controller;

import com.zarpar.domain.Avaliacao;
import com.zarpar.dto.AvaliacaoRequest;
import com.zarpar.service.AvaliacaoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @GetMapping("/ponto/{pontoId}")
    public ResponseEntity<List<Avaliacao>> listar(@PathVariable Long pontoId) {
        return ResponseEntity.ok(service.listarPorPonto(pontoId));
    }

    @PostMapping("/ponto/{pontoId}")
    public ResponseEntity<?> salvar(
            @PathVariable Long pontoId,
            @Valid @RequestBody AvaliacaoRequest req,
            BindingResult result,
            HttpSession session
    ) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
        }

        Object userId = session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login necessário");

        Avaliacao salva = service.salvar(pontoId, (Long) userId, req);
        return ResponseEntity.ok(salva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable String id, HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login necessário");

        service.excluir(id, (Long) userId);
        return ResponseEntity.noContent().build();
    }
}