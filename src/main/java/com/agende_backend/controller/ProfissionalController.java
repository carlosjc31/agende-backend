package com.agende_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    // listar todos os profissionais
    @GetMapping
    public ResponseEntity<List<ProfissionalResponse>> listarTodos() {
        List<ProfissionalResponse> profissionais = profissionalService.listarTodosProfissionais();
        return ResponseEntity.ok(profissionais);
    }
    // buscar profissional por id
    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponse> buscarPorId(@PathVariable UUID id) {
        try {
            ProfissionalResponse profissional = profissionalService.buscarPorId(id);
            return ResponseEntity.ok(profissional);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    // buscar profissional por especialidade
    @GetMapping("/especialidade/{especialidade}")
    public ResponseEntity<List<ProfissionalResponse>> buscarPorEspecialidade(@PathVariable String especialidade) {
        List<ProfissionalResponse> profissionais = profissionalService.buscarPorEspecialidade(especialidade);
        return ResponseEntity.ok(profissionais);
    }
    // buscar profissional por nome
    @GetMapping("/buscar")
    public ResponseEntity<List<ProfissionalResponse>> buscar(@RequestParam String q) {
        List<ProfissionalResponse> profissionais = profissionalService.buscarProfissionais(q);
        return ResponseEntity.ok(profissionais);
    }
    // atualizar profissional
    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalResponse> atualizarProfissional(
            @PathVariable UUID id,
            @RequestBody com.agende_backend.dto.AtualizarProfissionalRequest request) {
        try {
            ProfissionalResponse atualizado = profissionalService.atualizarProfissional(id, request);
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    // listar melhores avaliados
    @GetMapping("/top-avaliados")
    public ResponseEntity<List<ProfissionalResponse>> listarMelhoresAvaliados() {
        List<ProfissionalResponse> profissionais = profissionalService.listarMelhoresAvaliados();
        return ResponseEntity.ok(profissionais);
    }
    // listar profissionais pendentes
    @GetMapping("/pendentes")
    public ResponseEntity<List<ProfissionalResponse>> listarPendentes() {
        List<ProfissionalResponse> pendentes = profissionalService.listarPendentes();
        return ResponseEntity.ok(pendentes);
    }
    // aprovar profissional
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovarProfissional(@PathVariable UUID id) {
        profissionalService.aprovarProfissional(id);
        return ResponseEntity.ok().build();
    }
    // rejeitar profissional
    @PatchMapping("/{id}/rejeitar")
    public ResponseEntity<Void> rejeitarProfissional(@PathVariable UUID id) {
        profissionalService.rejeitarProfissional(id);
        return ResponseEntity.ok().build();
    }


}
