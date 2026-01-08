package com.agende_backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoResponse {

    private UUID id;
    private String tipo;
    private String titulo;
    private String mensagem;
    private Boolean lida;
    private LocalDateTime dataEnvio;
}
