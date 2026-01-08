package com.agende_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agende_backend.dto.ProfissionalResponse;
import com.agende_backend.service.ProfissionalService;

@RestController
@RequestMapping("/api/profissionais")
@CrossOrigin(origins = "*")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @GetMapping
    public ResponseEntity<List<ProfissionalResponse>> listarTodos() {
        List<ProfissionalResponse> profissionais = profissionalService.listarTodosProfissionais();
        return ResponseEntity.ok(profissionais);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponse> buscarPorId(@PathVariable UUID id) {
        try {
            ProfissionalResponse profissional = profissionalService.buscarPorId(id);
            return ResponseEntity.ok(profissional);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/especialidade/{especialidade}")
    public ResponseEntity<List<ProfissionalResponse>> buscarPorEspecialidade(@PathVariable String especialidade) {
        List<ProfissionalResponse> profissionais = profissionalService.buscarPorEspecialidade(especialidade);
        return ResponseEntity.ok(profissionais);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProfissionalResponse>> buscar(@RequestParam String q) {
        List<ProfissionalResponse> profissionais = profissionalService.buscarProfissionais(q);
        return ResponseEntity.ok(profissionais);
    }

    @GetMapping("/top-avaliados")
    public ResponseEntity<List<ProfissionalResponse>> listarMelhoresAvaliados() {
        List<ProfissionalResponse> profissionais = profissionalService.listarMelhoresAvaliados();
        return ResponseEntity.ok(profissionais);
    }
}
