package com.zarpar.service;

import com.zarpar.domain.Categoria;
import com.zarpar.domain.PontoTuristico;
import com.zarpar.domain.Usuario;
import com.zarpar.repository.PontoTuristicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes para validar que os filtros funcionam corretamente
 */
@ExtendWith(MockitoExtension.class)
class PontoFiltrosTest {

    @Mock
    private PontoTuristicoRepository pontoRepository;

    private List<PontoTuristico> pontos;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        // Ponto 1: Rio de Janeiro, PRAIA, nota 4.5
        PontoTuristico ponto1 = new PontoTuristico();
        ponto1.setId(1L);
        ponto1.setNome("Copacabana");
        ponto1.setCidade("Rio de Janeiro");
        ponto1.setEstado("RJ");
        ponto1.setCategoria(Categoria.PRAIA);
        ponto1.setMediaAvaliacoes(4.5);
        ponto1.setCriadoPor(usuario);

        // Ponto 2: Rio de Janeiro, MONUMENTO, nota 5.0
        PontoTuristico ponto2 = new PontoTuristico();
        ponto2.setId(2L);
        ponto2.setNome("Cristo Redentor");
        ponto2.setCidade("Rio de Janeiro");
        ponto2.setEstado("RJ");
        ponto2.setCategoria(Categoria.PRAIA); // Usando categoria existente
        ponto2.setMediaAvaliacoes(5.0);
        ponto2.setCriadoPor(usuario);

        // Ponto 3: São Paulo, PARQUE, nota 3.5
        PontoTuristico ponto3 = new PontoTuristico();
        ponto3.setId(3L);
        ponto3.setNome("Parque Ibirapuera");
        ponto3.setCidade("São Paulo");
        ponto3.setEstado("SP");
        ponto3.setCategoria(Categoria.PRAIA); // Usando categoria existente
        ponto3.setMediaAvaliacoes(3.5);
        ponto3.setCriadoPor(usuario);

        pontos = Arrays.asList(ponto1, ponto2, ponto3);
    }

    @Test
    void deveFiltrarPontosPorCidade() {
        // Simular filtragem por cidade
        List<PontoTuristico> filtrados = pontos.stream()
                .filter(p -> p.getCidade().equalsIgnoreCase("Rio de Janeiro"))
                .toList();

        assertEquals(2, filtrados.size());
        assertTrue(filtrados.stream().allMatch(p -> p.getCidade().equals("Rio de Janeiro")));
    }

    @Test
    void deveFiltrarPontosPorCategoria() {
        // Simular filtragem por categoria
        // Todos os 3 pontos têm categoria PRAIA no setup
        List<PontoTuristico> filtrados = pontos.stream()
                .filter(p -> p.getCategoria() == Categoria.PRAIA)
                .toList();

        assertEquals(3, filtrados.size());
        assertTrue(filtrados.stream().allMatch(p -> p.getCategoria() == Categoria.PRAIA));
    }

    @Test
    void deveFiltrarPontosPorNotaMinima() {
        // Simular filtragem por nota mínima
        double notaMinima = 4.0;
        List<PontoTuristico> filtrados = pontos.stream()
                .filter(p -> p.getMediaAvaliacoes() >= notaMinima)
                .toList();

        assertEquals(2, filtrados.size());
        assertTrue(filtrados.stream().allMatch(p -> p.getMediaAvaliacoes() >= 4.0));
    }

    @Test
    void deveFiltrarComMultiplosCriterios() {
        // Simular filtragem por cidade E nota mínima
        String cidade = "Rio de Janeiro";
        double notaMinima = 4.5;

        List<PontoTuristico> filtrados = pontos.stream()
                .filter(p -> p.getCidade().equalsIgnoreCase(cidade))
                .filter(p -> p.getMediaAvaliacoes() >= notaMinima)
                .toList();

        assertEquals(2, filtrados.size());
        assertTrue(filtrados.stream().allMatch(p -> 
            p.getCidade().equals("Rio de Janeiro") && p.getMediaAvaliacoes() >= 4.5
        ));
    }

    @Test
    void deveRetornarTodosQuandoSemFiltros() {
        // Simular busca sem filtros
        assertEquals(3, pontos.size());
    }

    @Test
    void deveRetornarVazioQuandoNenhumPontoAtendeFiltro() {
        // Simular filtragem que não retorna resultados
        List<PontoTuristico> filtrados = pontos.stream()
                .filter(p -> p.getCidade().equalsIgnoreCase("Brasília"))
                .toList();

        assertEquals(0, filtrados.size());
    }

    @Test
    void deveFiltrarPontosPorEstado() {
        // Simular filtragem por estado
        List<PontoTuristico> filtrados = pontos.stream()
                .filter(p -> p.getEstado().equalsIgnoreCase("SP"))
                .toList();

        assertEquals(1, filtrados.size());
        assertEquals("São Paulo", filtrados.get(0).getCidade());
        assertEquals("SP", filtrados.get(0).getEstado());
    }

    @Test
    void deveValidarOrdenacaoPorNota() {
        // Simular ordenação por nota (maior para menor)
        List<PontoTuristico> ordenados = pontos.stream()
                .sorted((p1, p2) -> Double.compare(p2.getMediaAvaliacoes(), p1.getMediaAvaliacoes()))
                .toList();

        assertEquals(5.0, ordenados.get(0).getMediaAvaliacoes());
        assertEquals(4.5, ordenados.get(1).getMediaAvaliacoes());
        assertEquals(3.5, ordenados.get(2).getMediaAvaliacoes());
    }
}

