package com.agende_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agende_backend.dto.AuthResponse;
import com.agende_backend.dto.LoginRequest;
import com.agende_backend.dto.RegisterPacienteRequest;
import com.agende_backend.dto.RegisterProfissionalRequest;
import com.agende_backend.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // O endpoint que faltava para a app conseguir criar a conta!
    @PostMapping("/register/paciente")
    public ResponseEntity<?> registerPaciente(@Valid @RequestBody RegisterPacienteRequest request) {
        try {
            AuthResponse response = authService.registerPaciente(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Retorna o erro específico (ex: "Email já registado") para a app mostrar ao utilizador
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // NOVO Endpoint para cadastrar o Médico
    @PostMapping("/register/profissional")
    public ResponseEntity<?> registerProfissional(@Valid @RequestBody RegisterProfissionalRequest request) {
        try {
            AuthResponse response = authService.registerProfissional(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Isto vai imprimir o erro gigante no terminal do Java
            return ResponseEntity.internalServerError().body("Erro no servidor: " + e.getMessage());
        }
    }
}
