package com.agende_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agende_backend.dto.AuthResponse;
import com.agende_backend.dto.ForgotPasswordRequestDTO;
import com.agende_backend.dto.LoginRequest;
import com.agende_backend.dto.RegisterProfissionalRequest;
import com.agende_backend.dto.RegistroInicialDTO;
import com.agende_backend.dto.AuthResponse.ResetPasswordRequestDTO;
import com.agende_backend.dto.CompletarPerfilPacienteDTO;
import com.agende_backend.dto.CompletarPerfilProfissionalDTO;
import com.agende_backend.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;
    // NOVO Endpoint de login
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
    public ResponseEntity<?> registerPaciente(@Valid @RequestBody RegistroInicialDTO request) {
        try {
          // verifica se o email ja existe na base de usuarios
            AuthResponse response = authService.registrarUsuarioInicial(request);
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
    // NOVO Endpoint quando o usuario esquecer a senha
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
      authService.processForgotPassword(request.getEmail());

      return ResponseEntity.ok("Se o e-mail estiver cadastrado, as instruções foram enviadas.");
    }
    // NOVO Endpoint para resetar a senha
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        boolean sucesso = authService.resetPassword(request.email(), request.codigo(), request.novaSenha(), authService.passwordEncoder);

        if (sucesso) {
            return ResponseEntity.ok("Senha alterada com sucesso.");
        }
        else{
          return ResponseEntity.badRequest().body("Código inválido ou expirado.");
        }
    }
    // NOVO Endpoint para completar o perfil
    @PutMapping("/completar-perfil/paciente")
    public ResponseEntity<String> completarPerfil(@Valid @RequestBody CompletarPerfilPacienteDTO request){
      try{
          authService.completarPerfilPaciente(request);
          return ResponseEntity.ok("Perfil completado com sucesso!");
      } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body("Erro ao completar o perfil: " + e.getMessage());
      }
    }
    // NOVO Endpoint para completar o perfil do profissional
    @PutMapping("/completar-perfil/profissional")
    public ResponseEntity<AuthResponse> completarPerfilProfissional(@RequestBody CompletarPerfilProfissionalDTO request){
        AuthResponse respose = authService.completarPerfilProfissional(request);
        return ResponseEntity.ok(respose);

    }
}
