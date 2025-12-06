package com.zarpar.service;

import com.zarpar.domain.Avaliacao;
import com.zarpar.domain.PontoTuristico;
import com.zarpar.domain.Role;
import com.zarpar.domain.Usuario;
import com.zarpar.dto.AvaliacaoRequest;
import com.zarpar.repository.AvaliacaoRepository;
import com.zarpar.repository.PontoTuristicoRepository;
import com.zarpar.repository.UsuarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final PontoTuristicoRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, PontoTuristicoRepository pontoRepository, UsuarioRepository usuarioRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.pontoRepository = pontoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Avaliacao> listarPorPonto(Long pontoId) {
        return avaliacaoRepository.findByPontoIdOrderByDataDesc(pontoId);
    }

    public Optional<Avaliacao> buscarMinhaAvaliacao(Long pontoId, Long usuarioId) {
        return avaliacaoRepository.findByPontoIdAndUsuarioId(pontoId, usuarioId);
    }

    @Transactional
    @CacheEvict(value = "pontos", allEntries = true)
    public Avaliacao salvar(Long pontoId, Long usuarioId, AvaliacaoRequest req) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        PontoTuristico ponto = pontoRepository.findById(pontoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ponto não encontrado"));

        Optional<Avaliacao> existente = avaliacaoRepository.findByPontoIdAndUsuarioId(pontoId, usuarioId);

        Avaliacao avaliacao;
        if (existente.isPresent()) {
            avaliacao = existente.get();
            avaliacao.setNota(req.getNota());
            avaliacao.setComentario(req.getComentario());

            avaliacao.setNomeUsuario(usuario.getNome());
        } else {
            avaliacao = new Avaliacao(pontoId, usuarioId, usuario.getNome(), req.getNota(), req.getComentario());
        }

        avaliacao = avaliacaoRepository.save(avaliacao);

        atualizarMediaPonto(ponto);

        return avaliacao;
    }

    @Transactional
    @CacheEvict(value = "pontos", allEntries = true)
    public void excluir(String id, Long usuarioId) {
        Avaliacao av = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!av.getUsuarioId().equals(usuarioId) && usuario.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissão");
        }

        Long pontoId = av.getPontoId();
        avaliacaoRepository.delete(av);

        PontoTuristico ponto = pontoRepository.findById(pontoId).orElseThrow();
        atualizarMediaPonto(ponto);
    }

    private void atualizarMediaPonto(PontoTuristico ponto) {
        List<Avaliacao> todas = avaliacaoRepository.findByPontoIdOrderByDataDesc(ponto.getId());

        if (todas.isEmpty()) {
            ponto.setMediaAvaliacoes(0.0);
        } else {
            double media = todas.stream()
                    .mapToInt(Avaliacao::getNota)
                    .average()
                    .orElse(0.0);

            ponto.setMediaAvaliacoes(Math.round(media * 10.0) / 10.0);
        }

        pontoRepository.save(ponto);
    }

    public void deletarTodasDoPonto(Long pontoId) {
        avaliacaoRepository.deleteByPontoId(pontoId);
    }
}