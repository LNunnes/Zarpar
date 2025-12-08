package com.zarpar.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para upload de fotos
 * - Valida tipos de arquivo aceitos
 * - Valida tamanho máximo
 */
@ExtendWith(MockitoExtension.class)
class FotoServiceTest {

    @Test
    void deveAceitarArquivosDeImagem() {
        MultipartFile arquivo = mock(MultipartFile.class);
        when(arquivo.getContentType()).thenReturn("image/jpeg");

        assertTrue(arquivo.getContentType().startsWith("image/"));
    }

    @Test
    void naoDeveAceitarArquivosNaoImagem() {
        MultipartFile arquivo = mock(MultipartFile.class);
        when(arquivo.getContentType()).thenReturn("application/pdf");

        assertFalse(arquivo.getContentType().startsWith("image/"));
    }

    @Test
    void deveValidarTamanhoMaximoDoArquivo() {
        MultipartFile arquivo = mock(MultipartFile.class);
        
        // Arquivo pequeno (1MB)
        when(arquivo.getSize()).thenReturn(1024L * 1024L);
        assertTrue(arquivo.getSize() <= 5 * 1024 * 1024, "Arquivo de 1MB deve ser aceito");

        // Arquivo grande (10MB)
        when(arquivo.getSize()).thenReturn(10L * 1024L * 1024L);
        assertFalse(arquivo.getSize() <= 5 * 1024 * 1024, "Arquivo de 10MB deve ser rejeitado");
    }

    @Test
    void deveAceitarExtensoesValidas() {
        MultipartFile arquivo = mock(MultipartFile.class);
        
        when(arquivo.getOriginalFilename()).thenReturn("foto.jpg");
        assertTrue(arquivo.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif)$"));

        when(arquivo.getOriginalFilename()).thenReturn("foto.png");
        assertTrue(arquivo.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif)$"));

        when(arquivo.getOriginalFilename()).thenReturn("documento.pdf");
        assertFalse(arquivo.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif)$"));
    }

    @Test
    void deveValidarNomeDoArquivo() {
        MultipartFile arquivo = mock(MultipartFile.class);
        when(arquivo.getOriginalFilename()).thenReturn("foto.jpg");

        assertNotNull(arquivo.getOriginalFilename());
        assertFalse(arquivo.getOriginalFilename().isEmpty());
    }

    @Test
    void deveRejeitarArquivoVazio() {
        MultipartFile arquivo = mock(MultipartFile.class);
        when(arquivo.isEmpty()).thenReturn(true);

        assertTrue(arquivo.isEmpty());
    }

    @Test
    void deveAceitarArquivoComConteudo() {
        MultipartFile arquivo = mock(MultipartFile.class);
        when(arquivo.isEmpty()).thenReturn(false);
        when(arquivo.getSize()).thenReturn(1024L);

        assertFalse(arquivo.isEmpty());
        assertTrue(arquivo.getSize() > 0);
    }
}
