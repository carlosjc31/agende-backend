package com.agende_backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agende_backend.entity.Avaliacao;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {

    List<Avaliacao> findByProfissionalIdOrderByDataAvaliacaoDesc(UUID profissionalId);

    Optional<Avaliacao> findByConsultaId(UUID consultaId);

    Boolean existsByConsultaId(UUID consultaId);
} 
