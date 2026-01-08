package com.agende_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConsultaRequest {

    @NotNull(message = "ID do profissional é obrigatório")
    private UUID profissionalId;

    @NotNull(message = "Data da consulta é obrigatória")
    @Future(message = "Data da consulta deve ser futura")
    private LocalDate dataConsulta;

    @NotNull(message = "Hora da consulta é obrigatória")
    private LocalTime horaConsulta;

    private String motivoConsulta;
}
