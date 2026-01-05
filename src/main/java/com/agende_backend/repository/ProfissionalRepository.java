package com.agende_backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agende_backend.entity.Profissional;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, UUID> {

    Optional<Profissional> findByUsuarioId(UUID usuarioId);

    Optional<Profissional> findByCrm(String crm);

    Boolean existsByCrm(String crm);

    List<Profissional> findByValidadoTrue();

    List<Profissional> findByEspecialidadeContainingIgnoreCaseAndValidadoTrue(String especialidade);

    @Query("SELECT p FROM Profissional p WHERE " +
           "(LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.especialidade) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND p.validado = true")
    List<Profissional> searchProfissionais(@Param("search") String search);

    @Query("SELECT p FROM Profissional p WHERE p.validado = true ORDER BY p.avaliacaoMedia DESC")
    List<Profissional> findTopRatedProfissionais();
}
