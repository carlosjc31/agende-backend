package com.agende_backend.service;

//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//import com.agende_backend.controller.RegisterPacienteRequest;
import com.agende_backend.dto.AuthResponse;
import com.agende_backend.dto.LoginRequest;
import com.agende_backend.entity.Paciente;
import com.agende_backend.entity.Usuario;
import com.agende_backend.repository.PacienteRepository;
import com.agende_backend.repository.UsuarioRepository;
import com.agende_backend.security.JwtUtil;

import jakarta.transaction.Transactional;
//import jakarta.validation.Valid;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getPerfil().name());

        java.util.UUID perfilId = null;
        if (usuario.getPerfil() == Usuario.PerfilUsuario.PACIENTE) {
            perfilId = pacienteRepository.findByUsuarioId(usuario.getId())
                    .map(Paciente::getId)
                    .orElse(null);
        }

        return new AuthResponse(
            token,
            usuario.getId(),
            usuario.getEmail(),
            usuario.getPerfil().name(),
            perfilId
        );
    }


    @Transactional
    public AuthResponse registerPaciente(com.agende_backend.dto.RegisterPacienteRequest request) {
        // Validações
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (pacienteRepository.existsByCpf(request.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil(Usuario.PerfilUsuario.PACIENTE);
        usuario.setAtivo(true);
        usuario = usuarioRepository.save(usuario);

        // Criar paciente
        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setNomeCompleto(request.getNomeCompleto());
        paciente.setCpf(request.getCpf());
        paciente.setDataNascimento(request.getDataNascimento());
        paciente.setTelefone(request.getTelefone());
        paciente.setCns(request.getCns());
        paciente.setEndereco(request.getEndereco());
        paciente.setCidade(request.getCidade());
        paciente.setEstado(request.getEstado());
        paciente.setCep(request.getCep());
        paciente = pacienteRepository.save(paciente);

        // Gerar token
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getPerfil().name());

        return new AuthResponse(
            token,
            usuario.getId(),
            usuario.getEmail(),
            usuario.getPerfil().name(),
            paciente.getId()
        );
    }

}
