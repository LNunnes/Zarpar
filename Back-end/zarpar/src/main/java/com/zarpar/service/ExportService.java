package com.zarpar.service;

import com.zarpar.domain.Hospedagem;
import com.zarpar.domain.PontoTuristico;
import com.zarpar.dto.PontoExportDTO;
import com.zarpar.dto.PontoExportDTO.HospedagemExportDTO;
import com.zarpar.repository.HospedagemRepository;
import com.zarpar.repository.PontoTuristicoRepository;
import com.zarpar.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportService {

    private final PontoTuristicoRepository pontoRepository;
    private final HospedagemRepository hospedagemRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public ExportService(PontoTuristicoRepository pontoRepository, 
                        HospedagemRepository hospedagemRepository,
                        AvaliacaoRepository avaliacaoRepository) {
        this.pontoRepository = pontoRepository;
        this.hospedagemRepository = hospedagemRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public List<PontoExportDTO> exportarTodosPontos() {
        List<PontoTuristico> pontos = pontoRepository.findAll();
        return pontos.stream()
            .map(this::converterParaExportDTO)
            .collect(Collectors.toList());
    }

    public PontoExportDTO exportarPonto(Long pontoId) {
        PontoTuristico ponto = pontoRepository.findById(pontoId)
            .orElseThrow(() -> new IllegalArgumentException("Ponto turístico não encontrado"));
        return converterParaExportDTO(ponto);
    }

    private PontoExportDTO converterParaExportDTO(PontoTuristico ponto) {
        PontoExportDTO dto = new PontoExportDTO();
        dto.setId(ponto.getId());
        dto.setNome(ponto.getNome());
        dto.setDescricao(ponto.getDescricao());
        dto.setCidade(ponto.getCidade());
        dto.setEstado(ponto.getEstado());
        dto.setPais(ponto.getPais());
        dto.setLatitude(ponto.getLatitude());
        dto.setLongitude(ponto.getLongitude());
        dto.setEndereco(ponto.getEndereco());
        dto.setCategoria(ponto.getCategoria() != null ? ponto.getCategoria().name() : null);
        dto.setComoChegar(ponto.getComoChegar());
        dto.setMediaAvaliacoes(ponto.getMediaAvaliacoes());
        
        // Contar quantidade de avaliações do MongoDB
        int quantidadeAvaliacoes = avaliacaoRepository.countByPontoId(ponto.getId());
        dto.setQuantidadeAvaliacoes(quantidadeAvaliacoes);

        // Carregar hospedagens
        List<Hospedagem> hospedagens = hospedagemRepository.findByPontoIdOrderByPrecoMedioAsc(ponto.getId());
        List<HospedagemExportDTO> hospedagensDTO = hospedagens.stream()
            .map(this::converterHospedagem)
            .collect(Collectors.toList());
        dto.setHospedagens(hospedagensDTO);

        return dto;
    }

    private HospedagemExportDTO converterHospedagem(Hospedagem h) {
        HospedagemExportDTO dto = new HospedagemExportDTO();
        dto.setNome(h.getNome());
        dto.setTipo(h.getTipo().name());
        dto.setPrecoMedio(h.getPrecoMedio());
        dto.setEndereco(h.getEndereco());
        dto.setTelefone(h.getTelefone());
        dto.setLinkReserva(h.getLinkReserva());
        return dto;
    }

    public String exportarParaJSON(List<PontoExportDTO> pontos) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        for (int i = 0; i < pontos.size(); i++) {
            PontoExportDTO p = pontos.get(i);
            json.append("  {\n");
            json.append("    \"id\": ").append(p.getId()).append(",\n");
            json.append("    \"nome\": ").append(toJsonString(p.getNome())).append(",\n");
            json.append("    \"descricao\": ").append(toJsonString(p.getDescricao())).append(",\n");
            json.append("    \"cidade\": ").append(toJsonString(p.getCidade())).append(",\n");
            json.append("    \"estado\": ").append(toJsonString(p.getEstado())).append(",\n");
            json.append("    \"pais\": ").append(toJsonString(p.getPais())).append(",\n");
            json.append("    \"latitude\": ").append(p.getLatitude()).append(",\n");
            json.append("    \"longitude\": ").append(p.getLongitude()).append(",\n");
            json.append("    \"endereco\": ").append(toJsonString(p.getEndereco())).append(",\n");
            json.append("    \"categoria\": ").append(toJsonString(p.getCategoria())).append(",\n");
            json.append("    \"comoChegar\": ").append(toJsonString(p.getComoChegar())).append(",\n");
            json.append("    \"mediaAvaliacoes\": ").append(p.getMediaAvaliacoes()).append(",\n");
            json.append("    \"quantidadeAvaliacoes\": ").append(p.getQuantidadeAvaliacoes()).append(",\n");
            json.append("    \"hospedagens\": [\n");
            
            List<HospedagemExportDTO> hospedagens = p.getHospedagens();
            for (int j = 0; j < hospedagens.size(); j++) {
                HospedagemExportDTO h = hospedagens.get(j);
                json.append("      {\n");
                json.append("        \"nome\": ").append(toJsonString(h.getNome())).append(",\n");
                json.append("        \"tipo\": ").append(toJsonString(h.getTipo())).append(",\n");
                json.append("        \"precoMedio\": ").append(h.getPrecoMedio()).append(",\n");
                json.append("        \"endereco\": ").append(toJsonString(h.getEndereco())).append(",\n");
                json.append("        \"telefone\": ").append(toJsonString(h.getTelefone())).append(",\n");
                json.append("        \"linkReserva\": ").append(toJsonString(h.getLinkReserva())).append("\n");
                json.append("      }");
                if (j < hospedagens.size() - 1) json.append(",");
                json.append("\n");
            }
            
            json.append("    ]\n");
            json.append("  }");
            if (i < pontos.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("]");
        return json.toString();
    }
    
    private String toJsonString(Object valor) {
        if (valor == null) return "null";
        String str = valor.toString();
        return "\"" + str.replace("\\", "\\\\")
                         .replace("\"", "\\\"")
                         .replace("\n", "\\n")
                         .replace("\r", "\\r")
                         .replace("\t", "\\t") + "\"";
    }

    public String exportarParaCSV(List<PontoExportDTO> pontos) {
        StringBuilder csv = new StringBuilder();
        
        // Cabeçalho
        csv.append("ID,Nome,Descrição,Cidade,Estado,País,Latitude,Longitude,Endereço,Categoria,Como Chegar,Média Avaliações,Quantidade Avaliações,Hospedagens\n");
        
        // Dados
        for (PontoExportDTO ponto : pontos) {
            csv.append(escaparCSV(ponto.getId()))
               .append(",")
               .append(escaparCSV(ponto.getNome()))
               .append(",")
               .append(escaparCSV(ponto.getDescricao()))
               .append(",")
               .append(escaparCSV(ponto.getCidade()))
               .append(",")
               .append(escaparCSV(ponto.getEstado()))
               .append(",")
               .append(escaparCSV(ponto.getPais()))
               .append(",")
               .append(escaparCSV(ponto.getLatitude()))
               .append(",")
               .append(escaparCSV(ponto.getLongitude()))
               .append(",")
               .append(escaparCSV(ponto.getEndereco()))
               .append(",")
               .append(escaparCSV(ponto.getCategoria()))
               .append(",")
               .append(escaparCSV(ponto.getComoChegar()))
               .append(",")
               .append(escaparCSV(ponto.getMediaAvaliacoes()))
               .append(",")
               .append(escaparCSV(ponto.getQuantidadeAvaliacoes()))
               .append(",")
               .append("\"").append(formatarHospedagensParaCSV(ponto.getHospedagens())).append("\"")
               .append("\n");
        }
        
        return csv.toString();
    }

    public String exportarParaXML(List<PontoExportDTO> pontos) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<pontos_turisticos>\n");
        
        for (PontoExportDTO ponto : pontos) {
            xml.append("  <ponto>\n");
            xml.append("    <id>").append(ponto.getId()).append("</id>\n");
            xml.append("    <nome>").append(escaparXML(ponto.getNome())).append("</nome>\n");
            xml.append("    <descricao>").append(escaparXML(ponto.getDescricao())).append("</descricao>\n");
            xml.append("    <cidade>").append(escaparXML(ponto.getCidade())).append("</cidade>\n");
            xml.append("    <estado>").append(escaparXML(ponto.getEstado())).append("</estado>\n");
            xml.append("    <pais>").append(escaparXML(ponto.getPais())).append("</pais>\n");
            xml.append("    <latitude>").append(ponto.getLatitude()).append("</latitude>\n");
            xml.append("    <longitude>").append(ponto.getLongitude()).append("</longitude>\n");
            xml.append("    <endereco>").append(escaparXML(ponto.getEndereco())).append("</endereco>\n");
            xml.append("    <categoria>").append(escaparXML(ponto.getCategoria())).append("</categoria>\n");
            xml.append("    <como_chegar>").append(escaparXML(ponto.getComoChegar())).append("</como_chegar>\n");
            xml.append("    <media_avaliacoes>").append(ponto.getMediaAvaliacoes()).append("</media_avaliacoes>\n");
            xml.append("    <quantidade_avaliacoes>").append(ponto.getQuantidadeAvaliacoes()).append("</quantidade_avaliacoes>\n");
            
            xml.append("    <hospedagens>\n");
            for (HospedagemExportDTO hosp : ponto.getHospedagens()) {
                xml.append("      <hospedagem>\n");
                xml.append("        <nome>").append(escaparXML(hosp.getNome())).append("</nome>\n");
                xml.append("        <tipo>").append(hosp.getTipo()).append("</tipo>\n");
                xml.append("        <preco_medio>").append(hosp.getPrecoMedio()).append("</preco_medio>\n");
                xml.append("        <endereco>").append(escaparXML(hosp.getEndereco())).append("</endereco>\n");
                xml.append("        <telefone>").append(escaparXML(hosp.getTelefone())).append("</telefone>\n");
                xml.append("        <link_reserva>").append(escaparXML(hosp.getLinkReserva())).append("</link_reserva>\n");
                xml.append("      </hospedagem>\n");
            }
            xml.append("    </hospedagens>\n");
            
            xml.append("  </ponto>\n");
        }
        
        xml.append("</pontos_turisticos>");
        return xml.toString();
    }

    public String gerarNomeArquivo(String formato, boolean unico) {
        String data = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        if (unico) {
            return String.format("ponto_%s.%s", data, formato.toLowerCase());
        }
        return String.format("pontos_turisticos_%s.%s", data, formato.toLowerCase());
    }

    private String escaparCSV(Object valor) {
        if (valor == null) return "";
        String str = valor.toString();
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    private String escaparXML(String valor) {
        if (valor == null) return "";
        return valor.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
    }

    private String formatarHospedagensParaCSV(List<HospedagemExportDTO> hospedagens) {
        if (hospedagens == null || hospedagens.isEmpty()) return "";
        return hospedagens.stream()
            .map(h -> String.format("%s (%s - R$%.2f)", h.getNome(), h.getTipo(), h.getPrecoMedio()))
            .collect(Collectors.joining("; "));
    }
}

