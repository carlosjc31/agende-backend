package com.agende_backend.dto;

import lombok.Data;

@Data
public class AdminDashboardStatsResponse {
    // Estatísticas gerais
    private Long totalUsuarios;
    private Long totalPacientes;
    private Long totalProfissionais;
    private Long profissionaisPendentes;
    private Long consultasHoje;
    private Long consultasTotal;
}
