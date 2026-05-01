package com.agende_backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//import com.agende_backend.controller.RegisterPacienteRequest;
import com.agende_backend.dto.AuthResponse;
import com.agende_backend.dto.CompletarPerfilPacienteDTO;
import com.agende_backend.dto.CompletarPerfilProfissionalDTO;
import com.agende_backend.dto.LoginRequest;
import com.agende_backend.dto.RegisterProfissionalRequest;
import com.agende_backend.dto.RegistroInicialDTO;
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
        // Verifica se a senha esta correta usando o passwordEncoder
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }
        // Gera o token a partir do email e do perfil
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getPerfil().name());
        // Retorna a resposta
        java.util.UUID perfilId = null;
        String nome = null;
        String telefone = null;
        // Pega o ID do paciente ou do profissional dependendo do perfil
        if (usuario.getPerfil() == Usuario.PerfilUsuario.PACIENTE) {
          var paciente = pacienteRepository.findByUsuarioId(usuario.getId());
          if (paciente.isPresent()) {
              var paxiente = paciente.get();
              perfilId = paxiente.getId();
              nome = paxiente.getNomeCompleto();
              telefone = paxiente.getTelefone();
          }
        }
        // Pega o ID do paciente ou do profissional dependendo do perfil
        else if (usuario.getPerfil() == Usuario.PerfilUsuario.PROFISSIONAL) {
          var profissional = profissionalRepository.findByUsuarioId(usuario.getId());
          if (profissional.isPresent()) {
              var prof = profissional.get();
              perfilId = prof.getId(); // Agora sim, pega o ID da tabela profissionais!
              nome = prof.getNomeCompleto();
              telefone = prof.getTelefone();
          }
        }
        // Retorna a resposta com o token gerado
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

    // registrar paciente no sistema
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
    // Método para processar a solicitação de recuperação de senha
    public void processForgotPassword(String email){
      Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email.trim().toLowerCase());
      // verifica se o usuário foi encontrado
      if(usuarioOpt.isPresent()){
        Usuario usuario = usuarioOpt.get();
        // gera um token de 6 dígitos aleatórios
        String codigo = String.format("%06d", new Random().nextInt(999999));
        // salva o token na base de dados
        System.out.println("🔒 RECUPERAÇÃO DE SENHA SOLICITADA");
        System.out.println("E-mail encontrado: " + usuario.getEmail());
        System.out.println("Token gerado: " + codigo);
        System.out.println("Link simulado: http://192.168.0.11:8081/reset-password?token=" + codigo);

      }
    }
    // Método para resetar a senha
    public boolean resetPassword(String email, String codigo, String novaSenha, PasswordEncoder passwordEncoder){
      Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email.trim().toLowerCase());
      // verifica se o usuário foi encontrado
      if(usuarioOpt.isPresent()){
        Usuario usuario = usuarioOpt.get();
        // verifica se o token é valido
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

    public void completarPerfilPaciente(CompletarPerfilPacienteDTO dto){
        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário nao encontrado"));

        Paciente paciente = pacienteRepository.findByUsuarioId(usuario.getId())
                .orElse(new Paciente());

        paciente.setUsuario(usuario);
        paciente.setNomeCompleto(dto.nomeCompleto());
        paciente.setCpf(dto.cpf());
        paciente.setCns(dto.cns());
        paciente.setTelefone(dto.telefone());
        paciente.setDataNascimento(dto.dataNascimento());

        usuarioRepository.save(usuario);
        pacienteRepository.save(paciente);
    }
    // Registro inicial de Usuários
    @Transactional
    public AuthResponse registrarUsuarioInicial(RegistroInicialDTO request){
        // 1. Verifica se o email já existe na base de Usuários
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Este email já está cadastrado");
        }
        // 2. Cria a conta de Login (Usuário)
        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        // define o perfil do usuário de acordo com o request recebido no DTO
        try {
          usuario.setPerfil(Usuario.PerfilUsuario.valueOf(request.perfil().toUpperCase()));
        } catch (IllegalArgumentException e) {
          throw new RuntimeException("Perfil inválido, Use PACIENTE ou PROFISSIONAL");
        }
        usuario.setAtivo(true);
        usuario = usuarioRepository.save(usuario);
        // gera o token a partir do email e do perfil
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getPerfil().name());
        // Retorna a resposta
        return new AuthResponse(
          token, usuario.getId(), usuario.getEmail(), usuario.getPerfil().name(), null, null, null);
    }

    @Transactional
    public void completarPerfilProfissional(CompletarPerfilProfissionalDTO dto) {
        // 1. Pega o e-mail do usuário que está logado e fazendo a requisição
        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Busca o Usuário no banco de dados
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Profissional profissional = profissionalRepository.findByUsuarioId(usuario.getId())
                .orElse(new Profissional());

        // 3. Atualiza o nome completo na tabela de Usuário (se necessário)
        profissional.setUsuario(usuario);
        profissional.setNomeCompleto(dto.getNomeCompleto());
        profissional.setTelefone(dto.getTelefone());

        usuarioRepository.save(usuario);
        profissionalRepository.save(profissional);

        // 5. Salva os dados específicos do médico
        profissional.setCrm(dto.getCrm());
        profissional.setEspecialidade(dto.getEspecialidade());
        // profissional.setValidado(false); // Mantém como pendente para o Admin aprovar depois!

        profissionalRepository.save(profissional);
    }
}
