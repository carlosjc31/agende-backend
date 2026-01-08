package com.agende_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaResponse {

    private UUID id;
    private LocalDate dataConsulta;
    private LocalTime horaConsulta;
    private String status;
    private String motivoConsulta;
    private String observacoes;
    private BigDecimal valor;
    private LocalDateTime dataCriacao;

    // Dados do Paciente
    private UUID pacienteId;
    private String pacienteNome;
    private String pacienteFoto;

    // Dados do Profissional
    private UUID profissionalId;
    private String profissionalNome;
    private String profissionalEspecialidade;
    private String profissionalFoto;
    private String profissionalHospital;
}
