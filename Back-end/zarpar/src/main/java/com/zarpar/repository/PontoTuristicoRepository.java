package com.zarpar.repository;

import com.zarpar.domain.Categoria;
import com.zarpar.domain.PontoTuristico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PontoTuristicoRepository extends JpaRepository<PontoTuristico, Long> {

    boolean existsByNomeIgnoreCaseAndCidadeIgnoreCase(String nome, String cidade);

    @Query("SELECT p FROM PontoTuristico p WHERE " +
            "(:cidade IS NULL OR LOWER(p.cidade) LIKE :cidade) AND " +
            "(:categoria IS NULL OR p.categoria = :categoria) AND " +
            "(:notaMinima IS NULL OR p.mediaAvaliacoes >= :notaMinima)")
    Page<PontoTuristico> buscarComFiltros(
            @Param("cidade") String cidade,
            @Param("categoria") Categoria categoria,
            @Param("notaMinima") Double notaMinima,
            Pageable pageable);
}