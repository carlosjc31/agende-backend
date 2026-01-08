package com.agende_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agende_backend.dto.NotificacaoResponse;
import com.agende_backend.service.NotificacaoService;

@RestController
@RequestMapping("/api/notificacoes")
@CrossOrigin(origins = "*")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacaoResponse>> listarNotificacoes(@PathVariable UUID usuarioId) {
        List<NotificacaoResponse> notificacoes = notificacaoService.listarNotificacoes(usuarioId);
        return ResponseEntity.ok(notificacoes);
    }

    @GetMapping("/usuario/{usuarioId}/nao-lidas")
    public ResponseEntity<List<NotificacaoResponse>> listarNaoLidas(@PathVariable UUID usuarioId) {
        List<NotificacaoResponse> notificacoes = notificacaoService.listarNotificacoesNaoLidas(usuarioId);
        return ResponseEntity.ok(notificacoes);
    }

    @PatchMapping("/{notificacaoId}/marcar-lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable UUID notificacaoId) {
        try {
            notificacaoService.marcarComoLida(notificacaoId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
