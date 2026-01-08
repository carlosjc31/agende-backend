package com.agende_backend.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.agende_backend.entity.Consulta;
import com.agende_backend.entity.Profissional;

public interface ConsultaRepository extends JpaRepository<Consulta, UUID> {
    
    List<Consulta> findByPacienteIdOrderByDataConsultaDescHoraConsultaDesc(UUID pacienteId);

    List<Consulta> findByProfissionalIdOrderByDataConsultaDescHoraConsultaDesc(UUID profissionalId);

    List<Consulta> findByPacienteIdAndStatusOrderByDataConsultaDescHoraConsultaDesc(
        UUID pacienteId, Consulta.StatusConsulta status);

    List<Consulta> findByProfissionalIdAndStatusInOrderByDataConsultaDescHoraConsultaDesc(
        UUID profissionalId, List<Consulta.StatusConsulta> status);

    @Query("SELECT c FROM Consulta c WHERE c.profissional.id = :profissionalId " +
           "AND c.dataConsulta = :data " +
           "AND c.horaConsulta = :hora " +
           "AND c.status NOT IN ('CANCELADA')")
    List<Consulta> findHorarioOcupado(@Param("profissionalId") UUID profissionalId,
                                       @Param("data") LocalDate data,
                                       @Param("hora") LocalTime hora);

    @Query("SELECT c FROM Consulta c WHERE c.dataConsulta = :data " +
           "AND c.lembreteEnviado = false " +
           "AND c.status IN ('AGENDADA', 'CONFIRMADA')")
    List<Consulta> findConsultasParaLembrete(@Param("data") LocalDate data);

    @Query("SELECT c FROM Consulta c WHERE c.paciente.id = :pacienteId " +
           "AND c.dataConsulta >= CURRENT_DATE " +
           "AND c.status IN ('AGENDADA', 'CONFIRMADA') " +
           "ORDER BY c.dataConsulta ASC, c.horaConsulta ASC")
    List<Consulta> findProximasConsultasPaciente(@Param("pacienteId") UUID pacienteId);
    
    long countByDataConsulta(LocalDate dataConsulta);

    long countByDataConsultaAndProfissionalId(LocalDate dataConsulta, UUID profissionalId);

    long countByDataConsultaAndProfissionalIdAndStatus(LocalDate dataConsulta, UUID profissionalId, Consulta.StatusConsulta status);

    List<Consulta> findByProfissional(Profissional profissional);
    
    
    
}
