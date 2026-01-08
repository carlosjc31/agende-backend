package com.agende_backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


import com.agende_backend.entity.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, UUID> {

    Optional<Paciente> findByUsuarioId(UUID usuarioId);

    Optional<Paciente> findByCpf(String cpf);

    Boolean existsByCpf(String cpf);

   
}
