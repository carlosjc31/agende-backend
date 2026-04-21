package com.agende_backend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public record CompletarPerfilPacienteDTO(
  @NotBlank(message = "O nome é obrigatório")
  String nomeCompleto,

  @NotBlank(message = "O telefone é obrigatório")
  String telefone,

  @NotBlank(message = "O CPF é obrigatório")
  String cpf,

  @NotNull(message = "A data de nascimento é obrigatória")
  @Past(message = "A data de nascimento deve ser no passado")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  LocalDate dataNascimento,

  @NotBlank(message = "O CNS é obrigatório")
  String cns
) {}
