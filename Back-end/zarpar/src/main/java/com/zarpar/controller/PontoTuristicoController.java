package com.zarpar.controller;

import com.zarpar.domain.PontoTuristico;
import com.zarpar.dto.PontoTuristicoRequest;
import com.zarpar.dto.PontoTuristicoResponse;
import com.zarpar.service.PontoTuristicoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pontos")
public class PontoTuristicoController {

    private final PontoTuristicoService service;

    public PontoTuristicoController(PontoTuristicoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<PontoTuristicoResponse>> listar(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PontoTuristicoResponse> page = service.listarTodos(pageable)
                .map(PontoTuristicoResponse::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoTuristicoResponse> buscarPorId(@PathVariable Long id) {
        PontoTuristico p = service.buscarPorId(id);
        return ResponseEntity.ok(new PontoTuristicoResponse(p));
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody PontoTuristicoRequest req,
            BindingResult result,
            HttpSession session
    ) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
        }

        Long userId = getUserId(session);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");

        PontoTuristico criado = service.criar(req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PontoTuristicoResponse(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PontoTuristicoRequest req,
            BindingResult result,
            HttpSession session
    ) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
        }

        Long userId = getUserId(session);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");

        PontoTuristico atualizado = service.atualizar(id, req, userId);
        return ResponseEntity.ok(new PontoTuristicoResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");

        service.excluir(id, userId);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(HttpSession session) {
        Object idObj = session.getAttribute("userId");
        return (idObj instanceof Long) ? (Long) idObj : null;
    }
}