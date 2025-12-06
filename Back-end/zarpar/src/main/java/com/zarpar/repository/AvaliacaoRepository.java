package com.zarpar.repository;

import com.zarpar.domain.Avaliacao;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends MongoRepository<Avaliacao, String> {

    List<Avaliacao> findByPontoIdOrderByDataDesc(Long pontoId);

    Optional<Avaliacao> findByPontoIdAndUsuarioId(Long pontoId, Long usuarioId);
}