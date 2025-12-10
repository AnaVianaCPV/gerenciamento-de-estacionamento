package com.viana.gerenciamento_de_estacionamento.infrastructure.repository;

import com.viana.gerenciamento_de_estacionamento.domain.ticket.Status;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.Ticket;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.ports.TicketRepository;
import com.viana.gerenciamento_de_estacionamento.infrastructure.dao.TicketJpaRepository;
import com.viana.gerenciamento_de_estacionamento.infrastructure.persistence.TicketEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TicketRepositoryImpl implements TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;
    private final TicketMapper ticketMapper;

    public TicketRepositoryImpl(TicketJpaRepository ticketJpaRepository, TicketMapper ticketMapper) {
        this.ticketJpaRepository = ticketJpaRepository;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity ticketEntity = ticketMapper.toEntity(ticket);
        TicketEntity savedEntity = ticketJpaRepository.save(ticketEntity);
        return ticketMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Ticket> findById(String id) {
        return ticketMapper.toDomainOptional(ticketJpaRepository.findById(id));
    }

    @Override
    public Optional<Ticket> findByVeiculoPlacaAndStatusNotFinalizado(String placa) {
        return ticketMapper.toDomainOptional(ticketJpaRepository.findByVeiculoPlacaAndStatusNot(placa, Status.FINALIZADO));
    }

    @Override
    public void delete(Ticket ticket) {
        TicketEntity ticketEntity = ticketMapper.toEntity(ticket);
        ticketJpaRepository.delete(ticketEntity);
    }
}