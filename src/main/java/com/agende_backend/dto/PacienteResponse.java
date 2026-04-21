package com.agende_backend.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteResponse {
  // Atributos do paciente
  private UUID id;
  private String nomeCompleto;
  private String cpf;
  private LocalDate dataNascimento;
  private String telefone;
  private String email;
  private String cns;
  private String endereco;
  private String cidade;
  private String estado;
  private String cep;
  private String fotoUrl;
}
