package com.agende_backend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agende_backend.dto.AtualizarPacienteRequest;
import com.agende_backend.dto.PacienteResponse;
import com.agende_backend.entity.Paciente;
import com.agende_backend.repository.PacienteRepository;

@Service
public class PacienteService {
    @Autowired
    private PacienteRepository pacienteRepository;

    public PacienteResponse buscarPorId(UUID id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        return convertToResponse(paciente);
    }

    public PacienteResponse atualizarPaciente(UUID id, AtualizarPacienteRequest request) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        // Atualiza apenas os campos que o paciente preencheu no aplicativo
        if (request.getTelefone() != null) paciente.setTelefone(request.getTelefone());
        if (request.getCns() != null) paciente.setCns(request.getCns());
        if (request.getEndereco() != null) paciente.setEndereco(request.getEndereco());
        if (request.getCidade() != null) paciente.setCidade(request.getCidade());
        if (request.getEstado() != null) paciente.setEstado(request.getEstado());
        if (request.getCep() != null) paciente.setCep(request.getCep());

        paciente = pacienteRepository.save(paciente);
        return convertToResponse(paciente);
    }

    // O nosso conversor automático (Igual fizemos no Profissional)
    private PacienteResponse convertToResponse(Paciente paciente) {
        PacienteResponse response = new PacienteResponse();
        response.setId(paciente.getId());
        response.setNomeCompleto(paciente.getNomeCompleto());
        response.setCpf(paciente.getCpf()); // O Hibernate desfaz a criptografia sozinho aqui!
        response.setDataNascimento(paciente.getDataNascimento());
        response.setTelefone(paciente.getTelefone());
        response.setCns(paciente.getCns());
        response.setEndereco(paciente.getEndereco());
        response.setCidade(paciente.getCidade());
        response.setEstado(paciente.getEstado());
        response.setCep(paciente.getCep());
        response.setFotoUrl(paciente.getFotoUrl());

        // Pega o email da relação com a tabela de Usuários
        if (paciente.getUsuario() != null) {
            response.setEmail(paciente.getUsuario().getEmail());
        }

        return response;
    }

    public List<PacienteResponse> listarTodos() {
      List<Paciente> listaDoBanco = pacienteRepository.findAll();
    System.out.println("Pacientes encontrados no banco: " + listaDoBanco.size()); // Log de debug

    return listaDoBanco.stream()
        .map(this::convertToResponse) // Use o conversor que já testamos
        .collect(Collectors.toList());
    }


}
