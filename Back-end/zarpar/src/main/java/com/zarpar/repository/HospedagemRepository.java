package com.zarpar.repository;

import com.zarpar.domain.Hospedagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HospedagemRepository extends JpaRepository<Hospedagem, Long> {
    List<Hospedagem> findByPontoIdOrderByPrecoMedioAsc(Long pontoId);
    
    void deleteByPontoId(Long pontoId);
}

