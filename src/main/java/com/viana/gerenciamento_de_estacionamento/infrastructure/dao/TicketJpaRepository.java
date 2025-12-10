package com.viana.gerenciamento_de_estacionamento.infrastructure.dao;

import com.viana.gerenciamento_de_estacionamento.domain.ticket.Status;
import com.viana.gerenciamento_de_estacionamento.infrastructure.persistence.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketJpaRepository extends JpaRepository<TicketEntity, String> {
    Optional<TicketEntity> findByVeiculoPlacaAndStatusNot(String placa, Status status);
}