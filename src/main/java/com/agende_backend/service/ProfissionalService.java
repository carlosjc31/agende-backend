package com.agende_backend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agende_backend.dto.ProfissionalResponse;
import com.agende_backend.entity.Profissional;
import com.agende_backend.repository.ProfissionalRepository;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    public List<ProfissionalResponse> listarTodosProfissionais() {
        return profissionalRepository.findByValidadoTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProfissionalResponse> buscarPorEspecialidade(String especialidade) {
        return profissionalRepository.findByEspecialidadeContainingIgnoreCaseAndValidadoTrue(especialidade).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProfissionalResponse> buscarProfissionais(String busca) {
        return profissionalRepository.searchProfissionais(busca).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProfissionalResponse> listarMelhoresAvaliados() {
        return profissionalRepository.findTopRatedProfissionais().stream()
                .limit(10)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public ProfissionalResponse buscarPorId(UUID id) {
        @SuppressWarnings("null")
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        return convertToResponse(profissional);
    }

    private ProfissionalResponse convertToResponse(Profissional profissional) {
        ProfissionalResponse response = new ProfissionalResponse();
        response.setId(profissional.getId());
        response.setNomeCompleto(profissional.getNomeCompleto());
        response.setCrm(profissional.getCrm());
        response.setEspecialidade(profissional.getEspecialidade());
        response.setTelefone(profissional.getTelefone());
        response.setBio(profissional.getBio());
        response.setAnosExperiencia(profissional.getAnosExperiencia());
        response.setValorConsulta(profissional.getValorConsulta());
        response.setFotoUrl(profissional.getFotoUrl());
        response.setHospitalClinica(profissional.getHospitalClinica());
        response.setEndereco(profissional.getEndereco());
        response.setCidade(profissional.getCidade());
        response.setEstado(profissional.getEstado());
        response.setAvaliacaoMedia(profissional.getAvaliacaoMedia());
        response.setTotalAvaliacoes(profissional.getTotalAvaliacoes());
        response.setTotalPacientes(profissional.getTotalPacientes());
        response.setDisponivel(true); // Pode adicionar lógica adicional
        return response;
    }
}
