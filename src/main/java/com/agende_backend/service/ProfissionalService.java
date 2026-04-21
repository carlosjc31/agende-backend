package com.agende_backend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agende_backend.dto.AtualizarProfissionalRequest;
import com.agende_backend.dto.ProfissionalResponse;
import com.agende_backend.entity.Profissional;
import com.agende_backend.repository.ProfissionalRepository;

import jakarta.transaction.Transactional;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;
    // Listar todos os médicos
    public List<ProfissionalResponse> listarTodosProfissionais() {
        return profissionalRepository.findByValidadoTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    // Listar todos os médicos por especialidade
    public List<ProfissionalResponse> buscarPorEspecialidade(String especialidade) {
        return profissionalRepository.findByEspecialidadeContainingIgnoreCaseAndValidadoTrue(especialidade).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    // Listar todos os médicos por busca
    public List<ProfissionalResponse> buscarProfissionais(String busca) {
        return profissionalRepository.searchProfissionais(busca).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    // Listar os 10 melhores avaliados
    public List<ProfissionalResponse> listarMelhoresAvaliados() {
        return profissionalRepository.findTopRatedProfissionais().stream()
                .limit(10)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    // Listar um médico pelo id do usuário
    public ProfissionalResponse buscarPorId(UUID id) {
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        return convertToResponse(profissional);
    }

    // Lista apenas os médicos que têm validado = false
    public List<ProfissionalResponse> listarPendentes() {
        List<Profissional> pendentes = profissionalRepository.findByValidadoFalse();
        return pendentes.stream()
                .map(this::convertToResponse) // Usa a mesma conversão que você já tem!
                .collect(Collectors.toList());
    }

    // Aprovar (Muda o validado para true)
    @Transactional
    public void aprovarProfissional(UUID id) {
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        profissional.setValidado(true);
        profissionalRepository.save(profissional);
    }

    // Rejeitar (Exclui o pré-cadastro)
    @Transactional
    public void rejeitarProfissional(UUID id) {
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        profissionalRepository.delete(profissional);
    }
    // dados do profissional para response
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
    // atualizar profissional pelo id
    public ProfissionalResponse atualizarProfissional(UUID id, AtualizarProfissionalRequest request) {
        Profissional profissional = profissionalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        // Atualiza apenas os campos que o app enviou
        if (request.getTelefone() != null) profissional.setTelefone(request.getTelefone());
        if (request.getBio() != null) profissional.setBio(request.getBio());
        if (request.getValorConsulta() != null) profissional.setValorConsulta(request.getValorConsulta());
        if (request.getHospitalClinica() != null) profissional.setHospitalClinica(request.getHospitalClinica());
        if (request.getEndereco() != null) profissional.setEndereco(request.getEndereco());
        if (request.getCidade() != null) profissional.setCidade(request.getCidade());
        if (request.getEstado() != null) profissional.setEstado(request.getEstado());
        if (request.getCep() != null) profissional.setCep(request.getCep());

        profissionalRepository.save(profissional);

        // Retorna os dados atualizados (use o seu método existente de converter para Response)
        return convertToResponse(profissional);
    }
}
