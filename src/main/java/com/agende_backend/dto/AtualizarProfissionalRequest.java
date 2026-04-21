package com.agende_backend.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AtualizarProfissionalRequest {
  // atualizar profissional
  private String telefone;
  private String bio;
  private BigDecimal valorConsulta;
  private String hospitalClinica;
  private String endereco;
  private String cidade;
  private String estado;
  private String cep;
}
