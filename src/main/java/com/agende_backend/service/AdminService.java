package com.agende_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.agende_backend.dto.DashboardStatsResponse;
import com.agende_backend.dto.ProfissionalResponse;
import com.agende_backend.entity.Notificacao;
import com.agende_backend.entity.Profissional;
import com.agende_backend.entity.Usuario;
import com.agende_backend.repository.ConsultaRepository;
import com.agende_backend.repository.NotificacaoRepository;
import com.agende_backend.repository.PacienteRepository;
import com.agende_backend.repository.ProfissionalRepository;
import com.agende_backend.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();

        stats.setTotalUsuarios(usuarioRepository.count());
        stats.setTotalPacientes(pacienteRepository.count());
        stats.setTotalProfissionais(profissionalRepository.count());
        stats.setProfissionaisPendentes(profissionalRepository.countByValidado(false));

        LocalDate hoje = LocalDate.now();
        stats.setConsultasHoje(consultaRepository.countByDataConsulta(hoje));
        stats.setConsultasTotal(consultaRepository.count());

        return stats;
    }

    public List<ProfissionalResponse> listarProfissionaisPendentes() {
        List<Profissional> profissionais = profissionalRepository.findByValidado(false);

        return profissionais.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public void aprovarProfissional(UUID profissionalId) {

        @SuppressWarnings("null")
        Profissional profissional = profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        profissional.setValidado(true);
        profissionalRepository.save(profissional);

        // Criar notificação para o profissional
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(profissional.getUsuario());
        notificacao.setTitulo("Cadastro Aprovado!");
        notificacao.setMensagem("Seu cadastro foi aprovado. Agora você pode receber consultas.");

        notificacao.setTipo(Notificacao.TipoNotificacao.APROVACAO);
        notificacao.setLida(false);
        notificacao.setDataEnvio(LocalDateTime.now());
        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void rejeitarProfissional(UUID profissionalId, String motivo) {
        @SuppressWarnings("null")
        Profissional profissional = profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        // Criar notificação sobre rejeição
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(profissional.getUsuario());
        notificacao.setTitulo("Cadastro não aprovado");
        notificacao.setMensagem("Seu cadastro não foi aprovado. Motivo: " + motivo);

        notificacao.setTipo(Notificacao.TipoNotificacao.REJEICAO);
        notificacao.setLida(false);
        notificacao.setDataEnvio(LocalDateTime.now());
        notificacaoRepository.save(notificacao);

        // Opcional: desativar ou deletar o profissional
        profissionalRepository.delete(profissional);
    }

    public List<?> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    @SuppressWarnings("null")
    @Transactional
    public void deletarUsuario(UUID usuarioId) {
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuarioRepository.delete(usuario);
    }

    private ProfissionalResponse convertToResponse(Profissional profissional) {
        ProfissionalResponse response = new ProfissionalResponse();
        response.setId(profissional.getId());
        response.setNomeCompleto(profissional.getNomeCompleto());
        response.setEspecialidade(profissional.getEspecialidade());
        response.setEmail(profissional.getUsuario().getEmail());
        response.setTelefone(profissional.getTelefone());
        response.setValidado(profissional.getValidado());
        response.setAvaliacaoMedia(profissional.getAvaliacaoMedia());

        String crm = profissional.getCrm(); //é usado no ProfissionalService
        if(crm != null) {
            String[] parts = crm.split("[/\\-\\s]");
            if (parts.length >= 2) {
                response.setNumeroRegistro(parts[0]);
                response.setUfRegistro(parts[1]);
            } else {
                response.setNumeroRegistro(crm);
                response.setUfRegistro(null);
            }
        }

        return response;
    }

}

