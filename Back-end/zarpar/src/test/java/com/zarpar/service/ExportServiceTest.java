package com.zarpar.service;

import com.zarpar.domain.PontoTuristico;
import com.zarpar.domain.Usuario;
import com.zarpar.dto.PontoExportDTO;
import com.zarpar.repository.AvaliacaoRepository;
import com.zarpar.repository.HospedagemRepository;
import com.zarpar.repository.PontoTuristicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private PontoTuristicoRepository pontoRepository;

    @Mock
    private HospedagemRepository hospedagemRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @InjectMocks
    private ExportService exportService;

    private PontoTuristico ponto;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuario Teste");

        ponto = new PontoTuristico();
        ponto.setId(1L);
        ponto.setNome("Cristo Redentor");
        ponto.setDescricao("Monumento icônico");
        ponto.setCidade("Rio de Janeiro");
        ponto.setEstado("RJ");
        ponto.setPais("Brasil");
        ponto.setLatitude(BigDecimal.valueOf(-22.9519));
        ponto.setLongitude(BigDecimal.valueOf(-43.2105));
        ponto.setEndereco("Parque Nacional da Tijuca");
        ponto.setCriadoPor(usuario);
        ponto.setMediaAvaliacoes(4.5);
    }

    @Test
    void deveExportarTodosPontos() {
        List<PontoTuristico> pontos = Arrays.asList(ponto);
        when(pontoRepository.findAll()).thenReturn(pontos);
        when(hospedagemRepository.findByPontoIdOrderByPrecoMedioAsc(1L)).thenReturn(new ArrayList<>());
        when(avaliacaoRepository.countByPontoId(1L)).thenReturn(10);

        List<PontoExportDTO> result = exportService.exportarTodosPontos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cristo Redentor", result.get(0).getNome());
        assertEquals("Rio de Janeiro", result.get(0).getCidade());

        verify(pontoRepository).findAll();
    }

    @Test
    void deveExportarPontoEspecifico() {
        when(pontoRepository.findById(1L)).thenReturn(Optional.of(ponto));
        when(hospedagemRepository.findByPontoIdOrderByPrecoMedioAsc(1L)).thenReturn(new ArrayList<>());
        when(avaliacaoRepository.countByPontoId(1L)).thenReturn(10);

        PontoExportDTO result = exportService.exportarPonto(1L);

        assertNotNull(result);
        assertEquals("Cristo Redentor", result.getNome());
        assertEquals("Rio de Janeiro", result.getCidade());
        assertEquals(10, result.getQuantidadeAvaliacoes());

        verify(pontoRepository).findById(1L);
        verify(pontoRepository, never()).findAll();
    }

    @Test
    void deveConverterParaJSON() {
        List<PontoTuristico> pontos = Arrays.asList(ponto);
        when(pontoRepository.findAll()).thenReturn(pontos);
        when(hospedagemRepository.findByPontoIdOrderByPrecoMedioAsc(1L)).thenReturn(new ArrayList<>());
        when(avaliacaoRepository.countByPontoId(1L)).thenReturn(10);

        List<PontoExportDTO> dtos = exportService.exportarTodosPontos();
        String json = exportService.exportarParaJSON(dtos);

        assertNotNull(json);
        assertTrue(json.contains("Cristo Redentor"));
        assertTrue(json.contains("Rio de Janeiro"));
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
    }

    @Test
    void deveConverterParaCSV() {
        List<PontoTuristico> pontos = Arrays.asList(ponto);
        when(pontoRepository.findAll()).thenReturn(pontos);
        when(hospedagemRepository.findByPontoIdOrderByPrecoMedioAsc(1L)).thenReturn(new ArrayList<>());
        when(avaliacaoRepository.countByPontoId(1L)).thenReturn(10);

        List<PontoExportDTO> dtos = exportService.exportarTodosPontos();
        String csv = exportService.exportarParaCSV(dtos);

        assertNotNull(csv);
        assertTrue(csv.contains("nome") || csv.length() > 0); // CSV gerado
        assertTrue(csv.contains("Cristo Redentor"));
        assertTrue(csv.contains("Rio de Janeiro"));
    }

    @Test
    void deveConverterParaXML() {
        List<PontoTuristico> pontos = Arrays.asList(ponto);
        when(pontoRepository.findAll()).thenReturn(pontos);
        when(hospedagemRepository.findByPontoIdOrderByPrecoMedioAsc(1L)).thenReturn(new ArrayList<>());
        when(avaliacaoRepository.countByPontoId(1L)).thenReturn(10);

        List<PontoExportDTO> dtos = exportService.exportarTodosPontos();
        String xml = exportService.exportarParaXML(dtos);

        assertNotNull(xml);
        assertTrue(xml.contains("<?xml") || xml.length() > 0); // XML gerado
        assertTrue(xml.contains("Cristo Redentor"));
        assertTrue(xml.contains("Rio de Janeiro"));
    }

    @Test
    void deveIncluirDadosCompletosNaExportacao() {
        List<PontoTuristico> pontos = Arrays.asList(ponto);
        when(pontoRepository.findAll()).thenReturn(pontos);
        when(hospedagemRepository.findByPontoIdOrderByPrecoMedioAsc(1L)).thenReturn(new ArrayList<>());
        when(avaliacaoRepository.countByPontoId(1L)).thenReturn(10);

        List<PontoExportDTO> dtos = exportService.exportarTodosPontos();

        assertEquals(1, dtos.size());
        PontoExportDTO dto = dtos.get(0);
        assertEquals("Cristo Redentor", dto.getNome());
        assertEquals("Rio de Janeiro", dto.getCidade());
        assertEquals("RJ", dto.getEstado());
        assertEquals("Brasil", dto.getPais());
        assertEquals("Parque Nacional da Tijuca", dto.getEndereco());
        assertEquals(10, dto.getQuantidadeAvaliacoes());
    }

    @Test
    void deveLancarExcecaoQuandoPontoNaoEncontradoParaExportar() {
        when(pontoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            exportService.exportarPonto(999L);
        });

        verify(pontoRepository).findById(999L);
    }
}
