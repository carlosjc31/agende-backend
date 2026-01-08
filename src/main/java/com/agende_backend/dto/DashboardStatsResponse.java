package com.agende_backend.dto;

import lombok.Data;

@Data
public class DashboardStatsResponse {
    
    private Long totalUsuarios;
    private Long totalPacientes;
    private Long totalProfissionais;
    private Long profissionaisPendentes;
    private Long consultasHoje;
    private Long consultasTotal;
}
