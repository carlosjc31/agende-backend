package com.agende_backend.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class PacienteResponse {

    private UUID id;
    private String nomeCompleto;
    private String cpf;
    private LocalDate dataNascimento;
    private String telefone;
    private String cns;
    private String endereco;
    private String cidade;
    private String estado;
    private String cep;
    private String fotoUrl;
    private String email;
}
