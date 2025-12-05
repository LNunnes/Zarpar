package com.zarpar.repository;

import com.zarpar.domain.PontoTuristico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PontoTuristicoRepository extends JpaRepository<PontoTuristico, Long> {

    boolean existsByNomeIgnoreCaseAndCidadeIgnoreCase(String nome, String cidade);

    Page<PontoTuristico> findAll(Pageable pageable);
}