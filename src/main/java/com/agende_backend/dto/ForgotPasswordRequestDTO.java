package com.agende_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequestDTO {
  // esqueci minha senha, tem que enviar o e-mail
  @NotBlank(message = "E-mail é obrigatório")
  @Email(message = "Formato de e-mail inválido")
  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
