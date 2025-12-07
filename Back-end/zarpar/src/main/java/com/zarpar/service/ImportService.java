package com.zarpar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zarpar.domain.Categoria;
import com.zarpar.domain.PontoTuristico;
import com.zarpar.domain.Usuario;
import com.zarpar.dto.ImportResultDTO;
import com.zarpar.dto.PontoImportDTO;
import com.zarpar.repository.PontoTuristicoRepository;
import com.zarpar.repository.UsuarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportService {

    private final PontoTuristicoRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    public ImportService(PontoTuristicoRepository pontoRepository, 
                        UsuarioRepository usuarioRepository) {
        this.pontoRepository = pontoRepository;
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    @CacheEvict(value = "pontos", allEntries = true)
    public ImportResultDTO importarArquivo(MultipartFile file, Long usuarioId) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do arquivo inválido");
        }

        String extensao = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        
        List<PontoImportDTO> pontos;
        
        try {
            switch (extensao) {
                case "json":
                    pontos = parseJSON(file);
                    break;
                case "csv":
                    pontos = parseCSV(file);
                    break;
                case "xml":
                    pontos = parseXML(file);
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato não suportado. Use JSON, CSV ou XML.");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao processar arquivo: " + e.getMessage());
        }

        return processarPontos(pontos, usuarioId);
    }

    private List<PontoImportDTO> parseJSON(MultipartFile file) throws Exception {
        return objectMapper.readValue(file.getInputStream(), new TypeReference<List<PontoImportDTO>>() {});
    }

    private List<PontoImportDTO> parseCSV(MultipartFile file) throws Exception {
        List<PontoImportDTO> pontos = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isHeader = true;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (isHeader) {
                    isHeader = false;
                    continue; // Pula o cabeçalho
                }
                
                String[] campos = line.split(",");
                
                if (campos.length < 3) {
                    throw new Exception("Linha " + lineNumber + ": Formato inválido - mínimo 3 campos obrigatórios");
                }
                
                PontoImportDTO ponto = new PontoImportDTO();
                ponto.setNome(limparCampo(campos[0]));
                ponto.setDescricao(limparCampo(campos[1]));
                ponto.setCidade(limparCampo(campos[2]));
                
                if (campos.length > 3) ponto.setEstado(limparCampo(campos[3]));
                if (campos.length > 4) ponto.setPais(limparCampo(campos[4]));
                if (campos.length > 5) ponto.setEndereco(limparCampo(campos[5]));
                if (campos.length > 6) ponto.setComoChegar(limparCampo(campos[6]));
                if (campos.length > 7 && !campos[7].trim().isEmpty()) {
                    try {
                        ponto.setLatitude(new BigDecimal(limparCampo(campos[7])));
                    } catch (NumberFormatException e) {
                        // Ignora latitude inválida
                    }
                }
                if (campos.length > 8 && !campos[8].trim().isEmpty()) {
                    try {
                        ponto.setLongitude(new BigDecimal(limparCampo(campos[8])));
                    } catch (NumberFormatException e) {
                        // Ignora longitude inválida
                    }
                }
                if (campos.length > 9) ponto.setCategoria(limparCampo(campos[9]));
                
                pontos.add(ponto);
            }
        }
        
        return pontos;
    }

    private List<PontoImportDTO> parseXML(MultipartFile file) throws Exception {
        List<PontoImportDTO> pontos = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            StringBuilder xmlContent = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                xmlContent.append(line);
            }
            
            String xml = xmlContent.toString();
            
            // Parse simples de XML (procura por tags <pontoTuristico>)
            int startIndex = 0;
            while ((startIndex = xml.indexOf("<pontoTuristico>", startIndex)) != -1) {
                int endIndex = xml.indexOf("</pontoTuristico>", startIndex);
                
                if (endIndex == -1) {
                    throw new Exception("XML mal formatado: tag pontoTuristico não fechada");
                }
                
                String pontoXml = xml.substring(startIndex, endIndex + 17);
                PontoImportDTO ponto = parsePontoXML(pontoXml);
                pontos.add(ponto);
                
                startIndex = endIndex + 17;
            }
        }
        
        return pontos;
    }

    private PontoImportDTO parsePontoXML(String xml) {
        PontoImportDTO ponto = new PontoImportDTO();
        
        ponto.setNome(extrairTagXML(xml, "nome"));
        ponto.setDescricao(extrairTagXML(xml, "descricao"));
        ponto.setCidade(extrairTagXML(xml, "cidade"));
        ponto.setEstado(extrairTagXML(xml, "estado"));
        ponto.setPais(extrairTagXML(xml, "pais"));
        ponto.setEndereco(extrairTagXML(xml, "endereco"));
        ponto.setComoChegar(extrairTagXML(xml, "comoChegar"));
        ponto.setCategoria(extrairTagXML(xml, "categoria"));
        
        String latStr = extrairTagXML(xml, "latitude");
        if (latStr != null && !latStr.isEmpty()) {
            try {
                ponto.setLatitude(new BigDecimal(latStr));
            } catch (NumberFormatException e) {
                // Ignora
            }
        }
        
        String lonStr = extrairTagXML(xml, "longitude");
        if (lonStr != null && !lonStr.isEmpty()) {
            try {
                ponto.setLongitude(new BigDecimal(lonStr));
            } catch (NumberFormatException e) {
                // Ignora
            }
        }
        
        return ponto;
    }

    private String extrairTagXML(String xml, String tagName) {
        String openTag = "<" + tagName + ">";
        String closeTag = "</" + tagName + ">";
        
        int startIndex = xml.indexOf(openTag);
        int endIndex = xml.indexOf(closeTag);
        
        if (startIndex == -1 || endIndex == -1) {
            return null;
        }
        
        return xml.substring(startIndex + openTag.length(), endIndex).trim();
    }

    private String limparCampo(String campo) {
        if (campo == null) return null;
        
        // Remove aspas duplas no início e fim
        campo = campo.trim();
        if (campo.startsWith("\"") && campo.endsWith("\"")) {
            campo = campo.substring(1, campo.length() - 1);
        }
        
        return campo.trim();
    }

    private ImportResultDTO processarPontos(List<PontoImportDTO> pontos, Long usuarioId) {
        ImportResultDTO resultado = new ImportResultDTO();
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário inválido"));
        
        int linhaAtual = 1;
        for (PontoImportDTO dto : pontos) {
            resultado.incrementarTotal();
            
            try {
                // Validar campos obrigatórios
                if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
                    resultado.adicionarErro("Registro " + linhaAtual + ": Nome é obrigatório");
                    linhaAtual++;
                    continue;
                }
                
                if (dto.getDescricao() == null || dto.getDescricao().trim().isEmpty()) {
                    resultado.adicionarErro("Registro " + linhaAtual + ": Descrição é obrigatória");
                    linhaAtual++;
                    continue;
                }
                
                if (dto.getCidade() == null || dto.getCidade().trim().isEmpty()) {
                    resultado.adicionarErro("Registro " + linhaAtual + ": Cidade é obrigatória");
                    linhaAtual++;
                    continue;
                }
                
                // Verificar se já existe (mesmo nome e cidade)
                PontoTuristico pontoExistente = pontoRepository
                        .findByNomeIgnoreCaseAndCidadeIgnoreCase(dto.getNome(), dto.getCidade())
                        .orElse(null);
                
                if (pontoExistente != null) {
                    // Atualizar ponto existente
                    atualizarPonto(pontoExistente, dto);
                    pontoRepository.save(pontoExistente);
                    resultado.incrementarSucesso();
                } else {
                    // Criar novo ponto
                    Categoria categoria = dto.getCategoriaEnum();
                    
                    PontoTuristico novoPonto = new PontoTuristico(
                        dto.getNome(),
                        dto.getDescricao(),
                        dto.getCidade(),
                        dto.getEstado(),
                        dto.getPais(),
                        dto.getEndereco(),
                        dto.getComoChegar(),
                        dto.getLatitude(),
                        dto.getLongitude(),
                        usuario,
                        categoria
                    );
                    
                    pontoRepository.save(novoPonto);
                    resultado.incrementarSucesso();
                }
                
            } catch (Exception e) {
                resultado.adicionarErro("Registro " + linhaAtual + ": " + e.getMessage());
            }
            
            linhaAtual++;
        }
        
        return resultado;
    }

    private void atualizarPonto(PontoTuristico ponto, PontoImportDTO dto) {
        ponto.setDescricao(dto.getDescricao());
        ponto.setEstado(dto.getEstado());
        ponto.setPais(dto.getPais());
        ponto.setEndereco(dto.getEndereco());
        ponto.setComoChegar(dto.getComoChegar());
        ponto.setLatitude(dto.getLatitude());
        ponto.setLongitude(dto.getLongitude());
        
        Categoria categoria = dto.getCategoriaEnum();
        if (categoria != null) {
            ponto.setCategoria(categoria);
        }
    }
}

