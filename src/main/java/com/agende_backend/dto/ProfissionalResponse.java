package com.agende_backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalResponse {

    private UUID id;
    private String nomeCompleto;
    // Usado no ProfissionalService
    private String crm;

    private String especialidade;
    private String telefone;
    private String bio;
    private Integer anosExperiencia;
    private BigDecimal valorConsulta;
    private String fotoUrl;
    private String hospitalClinica;
    private String endereco;
    private String cidade;
    private String estado;

    // Usado no ProfissionalService
    private BigDecimal avaliacaoMedia;
    private Integer totalAvaliacoes;
    private Integer totalPacientes;

    private Boolean disponivel;

    // Usado no AdminService (pendentes/aprovar/rejeitar)
    private String numeroRegistro;
    private String ufRegistro;
    private String email;
    private Boolean validado;
    
}
