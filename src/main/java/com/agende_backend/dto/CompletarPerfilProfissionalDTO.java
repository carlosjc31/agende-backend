package com.agende_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompletarPerfilProfissionalDTO {

  @NotBlank(message = "O nome completo é obrigatório")
  private String nomeCompleto;

  @NotBlank(message = "O CRM é obrigatório")
  private String crm;

  @NotBlank(message = "A especialidade é obrigatória")
  private String especialidade;

  @NotBlank(message = "O telefone é obrigatório")
  @Size(min = 10, max = 15, message = "Telefone inválido")
  private String telefone;

    // Getters e Setters
  public String getNomeCompleto() {
        return nomeCompleto;
    }

  public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

  public String getCrm() {
        return crm;
    }

  public void setCrm(String crm) {
        this.crm = crm;
    }

  public String getEspecialidade() {
        return especialidade;
    }

  public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

  public String getTelefone() {
        return telefone;
    }

  public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

}
