package com.agende_backend.dto;

import lombok.Data;

@Data
public class AtualizarPacienteRequest {
  // Campos que podem ser atualizados
  private String telefone;
  private String cns;
  private String endereco;
  private String cidade;
  private String estado;
  private String cep;

}
