package com.zarpar.service;

import com.zarpar.domain.Hospedagem;
import com.zarpar.domain.PontoTuristico;
import com.zarpar.domain.Usuario;
import com.zarpar.dto.HospedagemRequest;
import com.zarpar.repository.HospedagemRepository;
import com.zarpar.repository.PontoTuristicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class HospedagemService {

    private final HospedagemRepository hospedagemRepository;
    private final PontoTuristicoRepository pontoTuristicoRepository;

    public HospedagemService(HospedagemRepository hospedagemRepository, 
                           PontoTuristicoRepository pontoTuristicoRepository) {
        this.hospedagemRepository = hospedagemRepository;
        this.pontoTuristicoRepository = pontoTuristicoRepository;
    }

    public List<Hospedagem> listarPorPonto(Long pontoId) {
        return hospedagemRepository.findByPontoIdOrderByPrecoMedioAsc(pontoId);
    }

    public Hospedagem buscarPorId(Long id) {
        return hospedagemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Hospedagem não encontrada"));
    }

    @Transactional
    public Hospedagem criar(Long pontoId, HospedagemRequest request, Usuario usuario) {
        PontoTuristico ponto = pontoTuristicoRepository.findById(pontoId)
            .orElseThrow(() -> new IllegalArgumentException("Ponto turístico não encontrado"));

        Hospedagem hospedagem = new Hospedagem(
            ponto,
            request.getNome(),
            request.getEndereco(),
            request.getTelefone(),
            request.getPrecoMedio(),
            request.getTipo(),
            request.getLinkReserva(),
            usuario
        );

        return hospedagemRepository.save(hospedagem);
    }

    @Transactional
    public Hospedagem atualizar(Long id, HospedagemRequest request, Usuario usuario) {
        Hospedagem hospedagem = buscarPorId(id);

        // Verificar permissão
        if (!usuario.getRole().equals("ADMIN") && !hospedagem.getCriadoPor().getId().equals(usuario.getId())) {
            throw new SecurityException("Você não tem permissão para editar esta hospedagem");
        }

        hospedagem.setNome(request.getNome());
        hospedagem.setEndereco(request.getEndereco());
        hospedagem.setTelefone(request.getTelefone());
        hospedagem.setPrecoMedio(request.getPrecoMedio());
        hospedagem.setTipo(request.getTipo());
        hospedagem.setLinkReserva(request.getLinkReserva());

        return hospedagemRepository.save(hospedagem);
    }

    @Transactional
    public void excluir(Long id, Usuario usuario) {
        Hospedagem hospedagem = buscarPorId(id);

        // Verificar permissão
        if (!usuario.getRole().equals("ADMIN") && !hospedagem.getCriadoPor().getId().equals(usuario.getId())) {
            throw new SecurityException("Você não tem permissão para remover esta hospedagem");
        }

        hospedagemRepository.delete(hospedagem);
    }
}

