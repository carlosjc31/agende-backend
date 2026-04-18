package com.agende_backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//import com.agende_backend.controller.RegisterPacienteRequest;
import com.agende_backend.dto.AuthResponse;
import com.agende_backend.dto.LoginRequest;
import com.agende_backend.dto.RegisterProfissionalRequest;
import com.agende_backend.entity.Paciente;
import com.agende_backend.entity.Profissional;
import com.agende_backend.entity.Usuario;
import com.agende_backend.repository.PacienteRepository;
import com.agende_backend.repository.UsuarioRepository;
import com.agende_backend.security.JwtUtil;

import jakarta.transaction.Transactional;


@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private com.agende_backend.repository.ProfissionalRepository profissionalRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;

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
        String nome = null;
        String telefone = null;

        if (usuario.getPerfil() == Usuario.PerfilUsuario.PACIENTE) {
          var paciente = pacienteRepository.findByUsuarioId(usuario.getId());
          if (paciente.isPresent()) {
              var paxiente = paciente.get();
              perfilId = paxiente.getId();
              nome = paxiente.getNomeCompleto();
              telefone = paxiente.getTelefone();
          }
        }

        else if (usuario.getPerfil() == Usuario.PerfilUsuario.PROFISSIONAL) {
          var profissional = profissionalRepository.findByUsuarioId(usuario.getId());
          if (profissional.isPresent()) {
              var prof = profissional.get();
              perfilId = prof.getId(); // Agora sim, pega o ID da tabela profissionais!
              nome = prof.getNomeCompleto();
              telefone = prof.getTelefone();
          }
        }

        return new AuthResponse(
            token,
            usuario.getId(),
            usuario.getEmail(),
            usuario.getPerfil().name(),
            perfilId,
            nome,
            telefone
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
            paciente.getId(),
            paciente.getNomeCompleto(),
            paciente.getTelefone()
        );
    }

    // Adicione isto no seu AuthService.java
    @Transactional
    public AuthResponse registerProfissional(RegisterProfissionalRequest request) {
        // 1. Verifica se o email já existe na base de Usuários
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Este email já está cadastrado");
        }

        // 2. Cria a conta de Login (Usuário)
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil(Usuario.PerfilUsuario.PROFISSIONAL);
        usuario = usuarioRepository.save(usuario);

        // 3. Cria o Perfil do Médico
        Profissional profissional = new Profissional();
        profissional.setUsuario(usuario);
        profissional.setNomeCompleto(request.getNomeCompleto());
        profissional.setTelefone(request.getTelefone());
        profissional.setEspecialidade(request.getEspecialidade());
        profissional.setCrm(request.getCrm());
        profissionalRepository.save(profissional);

        // 4. Retorna a resposta (Adapte este retorno ao método que você já usa no Paciente)
        return new AuthResponse(
            " ", // Certifique-se de que a variável token existe, ou passe "" (vazio) se não fizer login automático
            usuario.getId(),
            usuario.getEmail(),
            usuario.getPerfil().name(), // Pega a Role (PROFISSIONAL)
            profissional.getId(), // O ID do médico
            profissional.getNomeCompleto(),
            profissional.getTelefone()
        );
    }

    public void processForgotPassword(String email){
      Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email.trim().toLowerCase());

      if(usuarioOpt.isPresent()){
        Usuario usuario = usuarioOpt.get();

        String codigo = String.format("%06d", new Random().nextInt(999999));

        System.out.println("🔒 RECUPERAÇÃO DE SENHA SOLICITADA");
        System.out.println("E-mail encontrado: " + usuario.getEmail());
        System.out.println("Token gerado: " + codigo);
        System.out.println("Link simulado: http://192.168.0.11:8081/reset-password?token=" + codigo);

      }
    }

    public boolean resetPassword(String email, String codigo, String novaSenha, PasswordEncoder passwordEncoder){
      Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email.trim().toLowerCase());

      if(usuarioOpt.isPresent()){
        Usuario usuario = usuarioOpt.get();

        if(codigo.equals(usuario.getCodigoRecuperacao()) &&
            usuario.getValidadeCodigo() != null &&
            usuario.getValidadeCodigo().isAfter(LocalDateTime.now())){
              usuario.setSenha(passwordEncoder.encode(novaSenha));

              usuario.setCodigoRecuperacao(null);
              usuario.setValidadeCodigo(null);

              usuarioRepository.save(usuario);
              return true;
            }
      }
      return false;
    }

}
