package com.agende_backend.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.*;

    public class RegisterPacienteRequest {
    @NotBlank(message = "Nome completo é obrigatório")
    private String nomeCompleto;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    private String cns;
    public String getCns() {
      return cns;
    }
    private String endereco;
    public String getEndereco() {
      return endereco;
    }
    private String cidade;
    public String getCidade() {
      return cidade;
    }
    private String estado;
    public String getEstado() {
      return estado;
    }
    private String cep;
    public String getCep() {
      return cep;
    }
    public String getEmail() {
        return email;
    }
    public String getCpf() {
        return cpf;
    }
    public CharSequence getSenha() {
        return senha;
    }
    public String getNomeCompleto() {
        return nomeCompleto;
    }
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    public String getTelefone() {
        return telefone;
    }

}
