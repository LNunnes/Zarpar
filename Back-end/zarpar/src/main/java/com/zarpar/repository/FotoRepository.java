package com.zarpar.repository;

import com.zarpar.domain.Foto;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FotoRepository extends MongoRepository<Foto, String> {
    List<Foto> findByPontoIdOrderByDataDesc(Long pontoId);

    long countByPontoId(Long pontoId);
}