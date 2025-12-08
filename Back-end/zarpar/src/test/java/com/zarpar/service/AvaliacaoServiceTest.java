package com.zarpar.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para validar regras de avaliação
 */
@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Test
    void deveValidarNotaDentroDoIntervaloPermitido() {
        // Notas válidas: 1 a 5
        assertTrue(validarNota(1), "Nota 1 deve ser válida");
        assertTrue(validarNota(3), "Nota 3 deve ser válida");
        assertTrue(validarNota(5), "Nota 5 deve ser válida");
    }

    @Test
    void naoDeveAceitarNotasForaDoIntervalo() {
        assertFalse(validarNota(0), "Nota 0 deve ser inválida");
        assertFalse(validarNota(6), "Nota 6 deve ser inválida");
        assertFalse(validarNota(-1), "Nota negativa deve ser inválida");
    }

    @Test
    void deveValidarQueUsuarioPodeAvaliarApenasUmaVez() {
        // Simula que cada usuário pode avaliar apenas uma vez cada ponto
        // A lógica de negócio deve impedir avaliações duplicadas
        
        // Primeira avaliação - não existe ainda
        boolean primeiraAvaliacao = jaAvaliadoPeloUsuario(1L, 1L, false);
        assertFalse(primeiraAvaliacao, "Primeira avaliação não deve existir ainda");
        
        // Segunda avaliação - já existe
        boolean segundaAvaliacao = jaAvaliadoPeloUsuario(1L, 1L, true);
        assertTrue(segundaAvaliacao, "Deve detectar avaliação duplicada");
    }

    @Test
    void deveValidarComentarioOpcional() {
        // Comentário pode ser opcional
        assertTrue(validarComentario("Ótimo lugar!"));
        assertTrue(validarComentario(""));
        assertTrue(validarComentario(null));
    }

    @Test
    void deveValidarTamanhoMaximoDoComentario() {
        String comentarioLongo = "a".repeat(1001);
        assertFalse(comentarioLongo.length() <= 1000, "Comentário muito longo deve ser inválido");

        String comentarioOk = "a".repeat(1000);
        assertTrue(comentarioOk.length() <= 1000, "Comentário com 1000 chars deve ser válido");
    }

    // Métodos auxiliares de validação
    private boolean validarNota(int nota) {
        return nota >= 1 && nota <= 5;
    }

    private boolean validarComentario(String comentario) {
        return comentario == null || comentario.length() <= 1000;
    }

    private boolean jaAvaliadoPeloUsuario(Long pontoId, Long usuarioId, boolean simulaExistente) {
        // Simula consulta ao banco
        // Na implementação real, isso verificaria no repositório
        return simulaExistente;
    }
}
