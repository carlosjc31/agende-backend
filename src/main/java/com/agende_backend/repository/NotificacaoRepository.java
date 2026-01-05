package com.agende_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agende_backend.entity.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {
    
    List<Notificacao> findByUsuarioIdOrderByDataEnvioDesc(UUID usuarioId);

    List<Notificacao> findByUsuarioIdAndLidaFalseOrderByDataEnvioDesc(UUID usuarioId);

    Long countByUsuarioIdAndLidaFalse(UUID usuarioId);
}
