package com.agende_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agende_backend.dto.DashboardStatsResponse;
import com.agende_backend.dto.ProfissionalResponse;
import com.agende_backend.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        try {
            DashboardStatsResponse stats = adminService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/profissionais/pendentes")
    public ResponseEntity<List<ProfissionalResponse>> listarProfissionaisPendentes() {
        List<ProfissionalResponse> profissionais = adminService.listarProfissionaisPendentes();
        return ResponseEntity.ok(profissionais);
    }

    @PatchMapping("/profissionais/{id}/aprovar")
    public ResponseEntity<Void> aprovarProfissional(@PathVariable UUID id) {
        try {
            adminService.aprovarProfissional(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/profissionais/{id}/rejeitar")
    public ResponseEntity<Void> rejeitarProfissional(
            @PathVariable UUID id,
            @RequestParam String motivo) {
        try {
            adminService.rejeitarProfissional(id, motivo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<?>> listarTodosUsuarios() {
        try {
            List<?> usuarios = adminService.listarTodosUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable UUID id) {
        try {
            adminService.deletarUsuario(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
