package com.agende_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistroInicialDTO(
  @NotBlank(message = "E-mail é obrigatório")
  @Email(message = "Formato de e-mail inválido")
  String email,

  @NotBlank(message = "Senha é obrigatória")
  String senha,

  @NotBlank(message = "O perfil (PACIENTE ou PROFISSIONAL) é obrigatório")
  String perfil
) {}
