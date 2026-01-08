package com.agende_backend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agende_backend.dto.NotificacaoResponse;
import com.agende_backend.entity.Consulta;
import com.agende_backend.entity.Notificacao;
import com.agende_backend.repository.NotificacaoRepository;

import jakarta.transaction.Transactional;



@Service

public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Transactional
    public void enviarNotificacaoConsultaAgendada(Consulta consulta) {
        // Notificação para o paciente
        Notificacao notifPaciente = new Notificacao();
        notifPaciente.setUsuario(consulta.getPaciente().getUsuario());
        notifPaciente.setTipo(Notificacao.TipoNotificacao.CONFIRMACAO);
        notifPaciente.setTitulo("Consulta Agendada");
        notifPaciente.setMensagem(String.format("Sua consulta com %s foi agendada para %s às %s",
            consulta.getProfissional().getNomeCompleto(),
            consulta.getDataConsulta(),
            consulta.getHoraConsulta()));
        notificacaoRepository.save(notifPaciente);

        // Notificação para o profissional
        Notificacao notifProf = new Notificacao();
        notifProf.setUsuario(consulta.getProfissional().getUsuario());
        notifProf.setTipo(Notificacao.TipoNotificacao.CONFIRMACAO);
        notifProf.setTitulo("Nova Consulta Agendada");
        notifProf.setMensagem(String.format("Consulta agendada com %s para %s às %s",
            consulta.getPaciente().getNomeCompleto(),
            consulta.getDataConsulta(),
            consulta.getHoraConsulta()));
        notificacaoRepository.save(notifProf);
    }

    @Transactional
    public void enviarNotificacaoCancelamento(Consulta consulta) {
        // Notificação para o paciente
        Notificacao notifPaciente = new Notificacao();
        notifPaciente.setUsuario(consulta.getPaciente().getUsuario());
        notifPaciente.setTipo(Notificacao.TipoNotificacao.CANCELAMENTO);
        notifPaciente.setTitulo("Consulta Cancelada");
        notifPaciente.setMensagem(String.format("Sua consulta com %s foi cancelada",
            consulta.getProfissional().getNomeCompleto()));
        notificacaoRepository.save(notifPaciente);

        // Notificação para o profissional
        Notificacao notifProf = new Notificacao();
        notifProf.setUsuario(consulta.getProfissional().getUsuario());
        notifProf.setTipo(Notificacao.TipoNotificacao.CANCELAMENTO);
        notifProf.setTitulo("Consulta Cancelada");
        notifProf.setMensagem(String.format("A consulta com %s foi cancelada",
            consulta.getPaciente().getNomeCompleto()));
        notificacaoRepository.save(notifProf);
    }

    public List<NotificacaoResponse> listarNotificacoes(UUID usuarioId) {
        return notificacaoRepository.findByUsuarioIdOrderByDataEnvioDesc(usuarioId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<NotificacaoResponse> listarNotificacoesNaoLidas(UUID usuarioId) {
        return notificacaoRepository.findByUsuarioIdAndLidaFalseOrderByDataEnvioDesc(usuarioId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void marcarComoLida(UUID notificacaoId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    private NotificacaoResponse convertToResponse(Notificacao notificacao) {
        return new NotificacaoResponse(
            notificacao.getId(),
            notificacao.getTipo().name(),
            notificacao.getTitulo(),
            notificacao.getMensagem(),
            notificacao.getLida(),
            notificacao.getDataEnvio()
        );
    }
}
