package com.agende_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agende_backend.dto.AuthResponse;
import com.agende_backend.dto.LoginRequest;
import com.agende_backend.dto.RegisterPacienteRequest;
import com.agende_backend.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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
}
