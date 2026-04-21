package com.agende_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agende_backend.dto.ConsultaRequest;
import com.agende_backend.dto.ConsultaResponse;
import com.agende_backend.dto.ProfissionalDashboardResponse;
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
    // Agendar consulta para um paciente e um profissional
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
        // Verificar se horário está ocupado
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
    // Listar consultas de um paciente
    public List<ConsultaResponse> listarConsultasPaciente(UUID pacienteId) {
        List<Consulta> consultas = consultaRepository.findByPacienteIdOrderByDataConsultaDescHoraConsultaDesc(pacienteId);
        return consultas.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    // Listar consultas próximas de um paciente
    public List<ConsultaResponse> listarProximasConsultasPaciente(UUID pacienteId) {
        List<Consulta> consultas = consultaRepository.findProximasConsultasPaciente(pacienteId);
        return consultas.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    // Cancelar consulta
    @Transactional
    public ConsultaResponse cancelarConsulta(UUID consultaId, String motivo) {
        // Buscar consulta
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
        // Verificar se consulta pode ser cancelada
        if (consulta.getStatus() == Consulta.StatusConsulta.CANCELADA) {
            throw new RuntimeException("Consulta já está cancelada");
        }
        // Verificar se consulta pode ser cancelada
        if (consulta.getStatus() == Consulta.StatusConsulta.REALIZADA) {
            throw new RuntimeException("Não é possível cancelar consulta já realizada");
        }
        // verificação do motivo de cancelamento
        consulta.setStatus(Consulta.StatusConsulta.CANCELADA);
        consulta.setDataCancelamento(LocalDateTime.now());
        consulta.setMotivoCancelamento(motivo);
        consulta = consultaRepository.save(consulta);

        // Enviar notificação
        notificacaoService.enviarNotificacaoCancelamento(consulta);

        return convertToResponse(consulta);
    }
    // dados da consulta
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
    // Listar consultas de um profissional
    public List<ConsultaResponse> listarConsultasProfissional(UUID profissionalId) {
    profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
      // Listar consultas de um profissional
    List<Consulta> consultas =
            consultaRepository.findByProfissionalIdOrderByDataConsultaDescHoraConsultaDesc(profissionalId);
      // Retornar consultas
    return consultas.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
}


// Confirmar consulta para um profissional
    @Transactional
    public ConsultaResponse confirmarConsulta(UUID consultaId) {
      // Buscar consulta e verificar se ela pode ser confirmada
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
      // Verificar se consulta pode ser cancelada
        if (consulta.getStatus() == Consulta.StatusConsulta.CANCELADA) {
            throw new RuntimeException("Não é possível confirmar uma consulta cancelada");
        }
        // Verificar se consulta pode ser realizada
        if (consulta.getStatus() == Consulta.StatusConsulta.REALIZADA) {
            throw new RuntimeException("Não é possível confirmar uma consulta já realizada");
        }
        // Verificar se consulta pode ser confirmada
        if (consulta.getStatus() == Consulta.StatusConsulta.CONFIRMADA) {
            throw new RuntimeException("Consulta já está confirmada");
        }
        // Verificar se consulta pode ser agendada
        if (consulta.getStatus() != Consulta.StatusConsulta.AGENDADA) {
            throw new RuntimeException("Apenas consultas agendadas podem ser confirmadas");
        }
        // Confirmar consulta e salvar
        consulta.setStatus(Consulta.StatusConsulta.CONFIRMADA);
        consultaRepository.save(consulta);

        // Enviar notificação
        notificacaoService.enviarNotificacaoConfirmacao(consulta);

        return convertToResponse(consulta);
    }

    // Marcar consulta como realizada com observações
    @Transactional
    public ConsultaResponse marcarRealizada(UUID consultaId, String observacoes) {
      // Buscar consulta
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
        // Verificar se consulta pode ser realizada
        consulta.setStatus(Consulta.StatusConsulta.REALIZADA);
      // verificar se possui observações
        if (observacoes != null && !observacoes.trim().isEmpty()) {
            consulta.setObservacoes(observacoes);
        }
        // salvar consulta
        consulta = consultaRepository.save(consulta);

        return convertToResponse(consulta);
    }
    // Dashboard profissional de um profissional
    public ProfissionalDashboardResponse getDashboardProfissional(UUID profissionalId) {
    LocalDate hoje = LocalDate.now();
      // Contar consultas de hoje
    long hojeCount = consultaRepository.countByDataConsultaAndProfissionalId(hoje, profissionalId);
      // Contar consultas pendentes
    long pendentes = consultaRepository.countByDataConsultaAndProfissionalIdAndStatus(
        hoje, profissionalId, Consulta.StatusConsulta.PENDENTE);
      // Contar consultas confirmadas
    long confirmadas = consultaRepository.countByDataConsultaAndProfissionalIdAndStatus(
        hoje, profissionalId, Consulta.StatusConsulta.CONFIRMADA);
      // Contar consultas realizadas
    long realizadas = consultaRepository.countByDataConsultaAndProfissionalIdAndStatus(
        hoje, profissionalId, Consulta.StatusConsulta.REALIZADA);
      // Contar consultas canceladas
    long canceladas = consultaRepository.countByDataConsultaAndProfissionalIdAndStatus(
        hoje, profissionalId, Consulta.StatusConsulta.CANCELADA);
      // Retornar dashboard de um profissional
    return new ProfissionalDashboardResponse(hojeCount, pendentes, confirmadas, realizadas, canceladas);
  }
  // Listar todas as consultas de hoje
  public List<ConsultaResponse> listarTodasConsultasDoDia() {
    LocalDate hoje = LocalDate.now();
    List<Consulta> consultas = consultaRepository.findByDataConsulta(hoje);
    // Retornar consultas de hoje
    return consultas.stream()
            .map(this::convertToResponse) // Usa o seu conversor que já extrai nomes de médico e paciente
            .collect(Collectors.toList());
}
  // Listar todas as consultas de hoje para o admin
  public Map<String, Long> getAdminStats(){
    Map<String, Long> stats = new HashMap<>();
    stats.put("consultasHoje", consultaRepository.countByDataConsulta(LocalDate.now()));
    stats.put("pendentes", profissionalRepository.countByValidado(false));
    stats.put("totalPacientes", pacienteRepository.count());
    stats.put("totalProfissionais", profissionalRepository.countByValidado(true));

    return stats;

  }

}
