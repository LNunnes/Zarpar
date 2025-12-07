package com.zarpar.controller;

import com.zarpar.domain.Role;
import com.zarpar.dto.ImportResultDTO;
import com.zarpar.service.ImportService;
import com.zarpar.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final ImportService importService;
    private final UsuarioService usuarioService;

    public ImportController(ImportService importService, UsuarioService usuarioService) {
        this.importService = importService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<?> importarPontos(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        try {
            // Verificar se é ADMIN
            Long userId = (Long) session.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body("Usuário não autenticado");
            }

            var usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            
            if (usuario.getRole() != Role.ADMIN) {
                return ResponseEntity.status(403).body("Apenas administradores podem importar dados");
            }

            // Validar arquivo
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Arquivo vazio");
            }

            // Processar importação
            ImportResultDTO resultado = importService.importarArquivo(file, userId);

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao importar: " + e.getMessage());
        }
    }
}

