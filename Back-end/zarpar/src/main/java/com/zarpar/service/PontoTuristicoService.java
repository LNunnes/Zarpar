package com.zarpar.service;

import com.zarpar.domain.Categoria;
import com.zarpar.domain.PontoTuristico;
import com.zarpar.domain.Role;
import com.zarpar.domain.Usuario;
import com.zarpar.dto.PontoTuristicoRequest;
import com.zarpar.repository.PontoTuristicoRepository;
import com.zarpar.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PontoTuristicoService {

    private final PontoTuristicoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public PontoTuristicoService(PontoTuristicoRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<PontoTuristico> listar(String cidade, Categoria categoria, Pageable pageable) {

        String termoCidade = null;

        if (cidade != null && !cidade.isBlank()) {
            termoCidade = "%" + cidade + "%";
        }

        return repository.buscarComFiltros(termoCidade, categoria, pageable);
    }

    public PontoTuristico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ponto turístico não encontrado"));
    }

    @Transactional
    public PontoTuristico criar(PontoTuristicoRequest req, Long usuarioId) {
        if (repository.existsByNomeIgnoreCaseAndCidadeIgnoreCase(req.getNome(), req.getCidade())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um ponto turístico com este nome nesta cidade.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário inválido"));

        PontoTuristico p = new PontoTuristico(
                req.getNome(), req.getDescricao(), req.getCidade(),
                req.getEstado(), req.getPais(), req.getEndereco(), req.getComoChegar(),
                req.getLatitude(), req.getLongitude(), usuario, req.getCategoria()
        );

        return repository.save(p);
    }

    @Transactional
    public PontoTuristico atualizar(Long id, PontoTuristicoRequest req, Long usuarioId) {
        PontoTuristico p = buscarPorId(id);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!p.getCriadoPor().getId().equals(usuarioId) && usuario.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para editar este ponto.");
        }

        p.setNome(req.getNome());
        p.setDescricao(req.getDescricao());
        p.setCidade(req.getCidade());
        p.setEstado(req.getEstado());
        p.setPais(req.getPais());
        p.setEndereco(req.getEndereco());
        p.setComoChegar(req.getComoChegar());
        p.setLatitude(req.getLatitude());
        p.setLongitude(req.getLongitude());
        p.setCategoria(req.getCategoria());

        return repository.save(p);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        PontoTuristico p = buscarPorId(id);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!p.getCriadoPor().getId().equals(usuarioId) && usuario.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este ponto.");
        }

        // TODO: Aqui futuramente excluiremos fotos (Disco) e comentários (MongoDB)
        repository.delete(p);
    }
}