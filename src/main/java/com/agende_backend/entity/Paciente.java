package com.agende_backend.entity;

import java.time.LocalDate;
import java.util.UUID;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Convert;
import com.agende_backend.security.AesEncryptor;

@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    private Usuario usuario;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Convert(converter = AesEncryptor.class)
    @Column(unique = true, nullable = false, length = 255) // Aumentamos o length porque o Base64 ocupa mais espaço
    private String cpf;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    private String telefone;

    @Convert(converter = AesEncryptor.class)
    @Column(length = 255)
    private String cns; // Cartão Nacional de Saúde

    @Column(columnDefinition = "TEXT")
    private String endereco;

    private String cidade;

    @Column(length = 2)
    private String estado;

    private String cep;

    @Column(name = "foto_url", columnDefinition = "TEXT")
    private String fotoUrl;

}

