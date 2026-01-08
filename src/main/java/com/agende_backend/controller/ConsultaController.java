package com.agende_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agende_backend.dto.ConsultaRequest;
import com.agende_backend.dto.ConsultaResponse;
import com.agende_backend.service.ConsultaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/consultas")
@CrossOrigin(origins = "*")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @PostMapping("/agendar/{pacienteId}")
    public ResponseEntity<ConsultaResponse> agendarConsulta(
            @PathVariable UUID pacienteId,
            @Valid @RequestBody ConsultaRequest request) {
        try {
            ConsultaResponse response = consultaService.agendarConsulta(pacienteId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ConsultaResponse>> listarConsultasPaciente(@PathVariable UUID pacienteId) {
        List<ConsultaResponse> consultas = consultaService.listarConsultasPaciente(pacienteId);
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/paciente/{pacienteId}/proximas")
    public ResponseEntity<List<ConsultaResponse>> listarProximasConsultas(@PathVariable UUID pacienteId) {
        List<ConsultaResponse> consultas = consultaService.listarProximasConsultasPaciente(pacienteId);
        return ResponseEntity.ok(consultas);
    }

    @PatchMapping("/{consultaId}/cancelar")
    public ResponseEntity<ConsultaResponse> cancelarConsulta(
            @PathVariable UUID consultaId,
            @RequestParam(required = false) String motivo) {
        try {
            ConsultaResponse response = consultaService.cancelarConsulta(consultaId, motivo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

@GetMapping("/profissional/{profissionalId}")
public ResponseEntity<List<ConsultaResponse>> listarConsultasProfissional(@PathVariable UUID profissionalId) {
    List<ConsultaResponse> consultas = consultaService.listarConsultasProfissional(profissionalId);
    return ResponseEntity.ok(consultas);
}

@PatchMapping("/{consultaId}/confirmar")
public ResponseEntity<ConsultaResponse> confirmarConsulta(@PathVariable UUID consultaId) {
    try {
        ConsultaResponse response = consultaService.confirmarConsulta(consultaId);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}

@PatchMapping("/{consultaId}/marcar-realizada")
public ResponseEntity<ConsultaResponse> marcarRealizada(@PathVariable UUID consultaId) {
    try {
        ConsultaResponse response = consultaService.marcarRealizada(consultaId);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
} 
    

}
