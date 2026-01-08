package com.agende_backend.entity;

import java.math.BigDecimal;
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

@Entity
@Table(name = "profissionais")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Profissional {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    private Usuario usuario;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(unique = true, nullable = false)
    private String crm;

    @Column(nullable = false)
    private String especialidade;

    private String telefone;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "anos_experiencia")
    private Integer anosExperiencia;

    @Column(name = "valor_consulta", precision = 10, scale = 2)
    private BigDecimal valorConsulta;

    @Column(name = "foto_url", columnDefinition = "TEXT")
    private String fotoUrl;

    @Column(name = "hospital_clinica")
    private String hospitalClinica;

    @Column(columnDefinition = "TEXT")
    private String endereco;

    private String cidade;

    @Column(length = 2)
    private String estado;

    private String cep;

    @Column(nullable = false)
    private Boolean validado = false;

    @Column(name = "avaliacao_media", precision = 3, scale = 2)
    private BigDecimal avaliacaoMedia = BigDecimal.ZERO;

    @Column(name = "total_avaliacoes")
    private Integer totalAvaliacoes = 0;

    @Column(name = "total_pacientes")
    private Integer totalPacientes = 0;

    
}
