package com.agende_backend.controller;
import java.util.List;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agende_backend.dto.AvaliacaoRequest;
import com.agende_backend.entity.Avaliacao;
import com.agende_backend.service.AvaliacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/avaliacoes")
@CrossOrigin(origins = "*")
public class AvaliacaoController {

     @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping("/paciente/{pacienteId}")
    public ResponseEntity<Avaliacao> avaliarConsulta(
            @PathVariable UUID pacienteId,
            @Valid @RequestBody AvaliacaoRequest request) {
        try {
            Avaliacao avaliacao = avaliacaoService.avaliarConsulta(pacienteId, request);
            return ResponseEntity.ok(avaliacao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoes(@PathVariable UUID profissionalId) {
        List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesProfissional(profissionalId);
        return ResponseEntity.ok(avaliacoes);
    }
}
