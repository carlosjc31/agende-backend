package com.agende_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agende_backend.dto.AtualizarPacienteRequest;
import com.agende_backend.dto.PacienteResponse;

import com.agende_backend.service.PacienteService;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {
    @Autowired
    private PacienteService pacienteService;

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> buscarPorId(@PathVariable UUID id) {
        try {
            PacienteResponse paciente = pacienteService.buscarPorId(id);
            return ResponseEntity.ok(paciente);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponse> atualizarPaciente(
            @PathVariable UUID id,
            @RequestBody AtualizarPacienteRequest request) {
        try {
            PacienteResponse atualizado = pacienteService.atualizarPaciente(id, request);
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponse>> listarTodos() {
    try {
        List<PacienteResponse> dtos = pacienteService.listarTodos();
        return ResponseEntity.ok(dtos);
    } catch (Exception e) {
      e.printStackTrace();
        return ResponseEntity.status(500).build();
    }
  }
}
