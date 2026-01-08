package com.agende_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agende_backend.dto.ConsultaRequest;
import com.agende_backend.dto.ConsultaResponse;
import com.agende_backend.entity.Consulta;
import com.agende_backend.entity.Paciente;
import com.agende_backend.entity.Profissional;
import com.agende_backend.repository.ConsultaRepository;
import com.agende_backend.repository.PacienteRepository;
import com.agende_backend.repository.ProfissionalRepository;

import jakarta.transaction.Transactional;

@Service
public class ConsultaService {
    
    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Transactional
    public ConsultaResponse agendarConsulta(UUID pacienteId, ConsultaRequest request) {
        // Buscar paciente e profissional
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Profissional profissional = profissionalRepository.findById(request.getProfissionalId())
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        // Verificar se horário está disponível
        List<Consulta> horariosOcupados = consultaRepository.findHorarioOcupado(
            profissional.getId(),
            request.getDataConsulta(),
            request.getHoraConsulta()
        );

        if (!horariosOcupados.isEmpty()) {
            throw new RuntimeException("Horário já está ocupado");
        }

        // Criar consulta
        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setProfissional(profissional);
        consulta.setDataConsulta(request.getDataConsulta());
        consulta.setHoraConsulta(request.getHoraConsulta());
        consulta.setMotivoConsulta(request.getMotivoConsulta());
        consulta.setStatus(Consulta.StatusConsulta.AGENDADA);
        consulta.setValor(profissional.getValorConsulta());
        consulta = consultaRepository.save(consulta);

        // Enviar notificação
        notificacaoService.enviarNotificacaoConsultaAgendada(consulta);

        return convertToResponse(consulta);
    }    
    public List<ConsultaResponse> listarConsultasPaciente(UUID pacienteId) {
        List<Consulta> consultas = consultaRepository.findByPacienteIdOrderByDataConsultaDescHoraConsultaDesc(pacienteId);
        return consultas.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ConsultaResponse> listarProximasConsultasPaciente(UUID pacienteId) {
        List<Consulta> consultas = consultaRepository.findProximasConsultasPaciente(pacienteId);
        return consultas.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ConsultaResponse cancelarConsulta(UUID consultaId, String motivo) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (consulta.getStatus() == Consulta.StatusConsulta.CANCELADA) {
            throw new RuntimeException("Consulta já está cancelada");
        }

        if (consulta.getStatus() == Consulta.StatusConsulta.REALIZADA) {
            throw new RuntimeException("Não é possível cancelar consulta já realizada");
        }

        consulta.setStatus(Consulta.StatusConsulta.CANCELADA);
        consulta.setDataCancelamento(LocalDateTime.now());
        consulta.setMotivoCancelamento(motivo);
        consulta = consultaRepository.save(consulta);

        // Enviar notificação
        notificacaoService.enviarNotificacaoCancelamento(consulta);

        return convertToResponse(consulta);
    }

    private ConsultaResponse convertToResponse(Consulta consulta) {
        ConsultaResponse response = new ConsultaResponse();
        response.setId(consulta.getId());
        response.setDataConsulta(consulta.getDataConsulta());
        response.setHoraConsulta(consulta.getHoraConsulta());
        response.setStatus(consulta.getStatus().name());
        response.setMotivoConsulta(consulta.getMotivoConsulta());
        response.setObservacoes(consulta.getObservacoes());
        response.setValor(consulta.getValor());
        response.setDataCriacao(consulta.getDataCriacao());

        // Dados do paciente
        response.setPacienteId(consulta.getPaciente().getId());
        response.setPacienteNome(consulta.getPaciente().getNomeCompleto());
        response.setPacienteFoto(consulta.getPaciente().getFotoUrl());

        // Dados do profissional
        response.setProfissionalId(consulta.getProfissional().getId());
        response.setProfissionalNome(consulta.getProfissional().getNomeCompleto());
        response.setProfissionalEspecialidade(consulta.getProfissional().getEspecialidade());
        response.setProfissionalFoto(consulta.getProfissional().getFotoUrl());
        response.setProfissionalHospital(consulta.getProfissional().getHospitalClinica());

        return response;
    }    

    public List<ConsultaResponse> listarConsultasProfissional(UUID profissionalId) {
    profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

    List<Consulta> consultas =
            consultaRepository.findByProfissionalIdOrderByDataConsultaDescHoraConsultaDesc(profissionalId);

    return consultas.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
}



    @Transactional
    public ConsultaResponse confirmarConsulta(UUID consultaId) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (consulta.getStatus() == Consulta.StatusConsulta.CANCELADA) {
            throw new RuntimeException("Não é possível confirmar uma consulta cancelada");
        }

        if (consulta.getStatus() == Consulta.StatusConsulta.REALIZADA) {
            throw new RuntimeException("Não é possível confirmar uma consulta já realizada");
        }

        if (consulta.getStatus() == Consulta.StatusConsulta.CONFIRMADA) {
            throw new RuntimeException("Consulta já está confirmada");
        }

        if (consulta.getStatus() != Consulta.StatusConsulta.AGENDADA) {
            throw new RuntimeException("Apenas consultas agendadas podem ser confirmadas");
        }

        consulta.setStatus(Consulta.StatusConsulta.CONFIRMADA);
        consultaRepository.save(consulta);

        notificacaoService.enviarNotificacaoConfirmacao(consulta);

        return convertToResponse(consulta);
    }


    @Transactional
    public ConsultaResponse marcarRealizada(UUID consultaId) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consulta.setStatus(Consulta.StatusConsulta.REALIZADA);
        consultaRepository.save(consulta);

        return convertToResponse(consulta);
    }
}
