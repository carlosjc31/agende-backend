package com.agende_backend.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tipo = "Bearer";
    private UUID usuarioId;
    private String email;
    private String perfil;
    private UUID perfilId; // ID do Paciente ou Profissional

    private String nome;
    private String telefone;

    public AuthResponse(String token, UUID usuarioId, String email, String perfil, UUID perfilId, String nome, String telefone) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.email = email;
        this.perfil = perfil;
        this.perfilId = perfilId;
        this.nome = nome;
        this.telefone = telefone;
    }

    public record ResetPasswordRequestDTO(
        String email,
        String codigo,
        String novaSenha
    ) {}
}
