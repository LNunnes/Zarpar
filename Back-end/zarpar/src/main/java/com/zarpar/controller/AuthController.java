package com.zarpar.controller;

import com.zarpar.dto.AuthResponse;
import com.zarpar.dto.LoginRequest;
import com.zarpar.dto.RegisterRequest;
import com.zarpar.domain.Usuario;
import com.zarpar.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid; // Importante
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult; // Importante
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req,
                                      BindingResult result,
                                      HttpSession session) {

        if (result.hasErrors()) {
            String erro = result.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(erro);
        }

        String email = req.getEmail().trim().toLowerCase();

        if (usuarioService.emailExists(email)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("E-mail já cadastrado");
        }

        Usuario u = usuarioService.register(req.getNome().trim(), email, req.getSenha());

        session.setAttribute("userId", u.getId());
        session.setAttribute("userRole", u.getRole().name());

        AuthResponse resp = new AuthResponse(
                u.getId(), u.getNome(), u.getEmail(), u.getRole().name(),
                "Conta criada com sucesso"
        );
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest req,
            BindingResult result,
            HttpSession session
    ) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
        }

        String email = req.getEmail().trim().toLowerCase();

        Optional<Usuario> opt = usuarioService.findByEmail(email);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        Usuario u = opt.get();
        if (!usuarioService.checkPassword(u, req.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta");
        }

        session.setAttribute("userId", u.getId());
        session.setAttribute("userRole", u.getRole().name());

        AuthResponse resp = new AuthResponse(u.getId(), u.getNome(), u.getEmail(), u.getRole().name(),
                "Login efetuado com sucesso");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(java.util.Collections.singletonMap("message", "Logout efetuado"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object idObj = session.getAttribute("userId");
        if (idObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");
        }
        Long userId = (Long) idObj;
        Optional<Usuario> opt = usuarioService.findById(userId);
        if (opt.isEmpty()) {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");
        }
        Usuario u = opt.get();
        AuthResponse resp = new AuthResponse(u.getId(), u.getNome(), u.getEmail(), u.getRole().name(), null);
        return ResponseEntity.ok(resp);
    }
}