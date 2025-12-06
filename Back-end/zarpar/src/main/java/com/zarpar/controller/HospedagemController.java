package com.zarpar.controller;

import com.zarpar.domain.Hospedagem;
import com.zarpar.domain.Usuario;
import com.zarpar.dto.HospedagemRequest;
import com.zarpar.dto.HospedagemResponse;
import com.zarpar.service.HospedagemService;
import com.zarpar.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pontos/{pontoId}/hospedagens")
public class HospedagemController {

    private final HospedagemService hospedagemService;
    private final UsuarioService usuarioService;

    public HospedagemController(HospedagemService hospedagemService, UsuarioService usuarioService) {
        this.hospedagemService = hospedagemService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<HospedagemResponse>> listar(@PathVariable Long pontoId) {
        List<Hospedagem> hospedagens = hospedagemService.listarPorPonto(pontoId);
        List<HospedagemResponse> response = hospedagens.stream()
            .map(HospedagemResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HospedagemResponse> buscarPorId(@PathVariable Long pontoId, @PathVariable Long id) {
        Hospedagem hospedagem = hospedagemService.buscarPorId(id);
        return ResponseEntity.ok(new HospedagemResponse(hospedagem));
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @PathVariable Long pontoId,
            @Valid @RequestBody HospedagemRequest request,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body("Usuário não autenticado");
            }

            Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            Hospedagem hospedagem = hospedagemService.criar(pontoId, request, usuario);
            return ResponseEntity.ok(new HospedagemResponse(hospedagem));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long pontoId,
            @PathVariable Long id,
            @Valid @RequestBody HospedagemRequest request,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body("Usuário não autenticado");
            }

            Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            Hospedagem hospedagem = hospedagemService.atualizar(id, request, usuario);
            return ResponseEntity.ok(new HospedagemResponse(hospedagem));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(
            @PathVariable Long pontoId,
            @PathVariable Long id,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body("Usuário não autenticado");
            }

            Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            hospedagemService.excluir(id, usuario);
            return ResponseEntity.ok("Hospedagem removida com sucesso");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

