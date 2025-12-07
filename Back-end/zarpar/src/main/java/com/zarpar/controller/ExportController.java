package com.zarpar.controller;

import com.zarpar.domain.Role;
import com.zarpar.dto.PontoExportDTO;
import com.zarpar.service.ExportService;
import com.zarpar.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;
    private final UsuarioService usuarioService;

    public ExportController(ExportService exportService, UsuarioService usuarioService) {
        this.exportService = exportService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> exportarTodos(
            @RequestParam String formato,
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
                return ResponseEntity.status(403).body("Apenas administradores podem exportar dados");
            }
            
            // Exportar todos os pontos
            List<PontoExportDTO> pontos = exportService.exportarTodosPontos();

            return gerarResposta(pontos, formato, false);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao exportar: " + e.getMessage());
        }
    }

    @GetMapping("/ponto/{id}")
    public ResponseEntity<?> exportarPonto(
            @PathVariable Long id,
            @RequestParam String formato,
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
                return ResponseEntity.status(403).body("Apenas administradores podem exportar dados");
            }

            // Exportar ponto específico
            PontoExportDTO ponto = exportService.exportarPonto(id);
            List<PontoExportDTO> pontos = Collections.singletonList(ponto);

            return gerarResposta(pontos, formato, true);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao exportar: " + e.getMessage());
        }
    }

    private ResponseEntity<?> gerarResposta(List<PontoExportDTO> pontos, String formato, boolean unico) {
        String nomeArquivo = exportService.gerarNomeArquivo(formato, unico);
        String conteudo;
        MediaType mediaType;

        switch (formato.toLowerCase()) {
            case "json":
                conteudo = exportService.exportarParaJSON(pontos);
                mediaType = MediaType.APPLICATION_JSON;
                break;
            
            case "csv":
                conteudo = exportService.exportarParaCSV(pontos);
                mediaType = MediaType.parseMediaType("text/csv");
                break;
            
            case "xml":
                conteudo = exportService.exportarParaXML(pontos);
                mediaType = MediaType.APPLICATION_XML;
                break;
            
            default:
                return ResponseEntity.badRequest().body("Formato inválido. Use: json, csv ou xml");
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
            .contentType(mediaType)
            .body(conteudo);
    }
}

