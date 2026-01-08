package com.agende_backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agende_backend.dto.AvaliacaoRequest;
import com.agende_backend.entity.Avaliacao;
import com.agende_backend.entity.Consulta;
import com.agende_backend.repository.AvaliacaoRepository;
import com.agende_backend.repository.ConsultaRepository;

import jakarta.transaction.Transactional;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Transactional
    public Avaliacao avaliarConsulta(UUID pacienteId, AvaliacaoRequest request) {
        // Verificar se consulta existe e pertence ao paciente
        Consulta consulta = consultaRepository.findById(request.getConsultaId())
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (!consulta.getPaciente().getId().equals(pacienteId)) {
            throw new RuntimeException("Consulta não pertence ao paciente");
        }

        if (consulta.getStatus() != Consulta.StatusConsulta.REALIZADA) {
            throw new RuntimeException("Só é possível avaliar consultas realizadas");
        }

        // Verificar se já foi avaliada
        if (avaliacaoRepository.existsByConsultaId(consulta.getId())) {
            throw new RuntimeException("Consulta já foi avaliada");
        }

        // Criar avaliação
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setConsulta(consulta);
        avaliacao.setPaciente(consulta.getPaciente());
        avaliacao.setProfissional(consulta.getProfissional());
        avaliacao.setNota(request.getNota());
        avaliacao.setComentario(request.getComentario());

        return avaliacaoRepository.save(avaliacao);
    }

    public List<Avaliacao> listarAvaliacoesProfissional(UUID profissionalId) {
        return avaliacaoRepository.findByProfissionalIdOrderByDataAvaliacaoDesc(profissionalId);
    }
}
