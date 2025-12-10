package com.viana.gerenciamento_de_estacionamento.domain.ticket.service;

import com.viana.gerenciamento_de_estacionamento.domain.ticket.Ticket;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.ports.OperadoraPagamento;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.ports.TicketRepository;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.exception.FalhaPagamentoException;

import java.time.LocalDateTime;
import java.time.Clock;


public class PagamentoService {

    private final OperadoraPagamento operadora;
    private final CalculadoraDePreco calculadoraDePreco;
    private final TicketRepository ticketRepository;
    private final Clock clock;


    public PagamentoService(OperadoraPagamento operadora, CalculadoraDePreco calculadoraDePreco, TicketRepository ticketRepository, Clock clock) {
        this.operadora = operadora;
        this.calculadoraDePreco = calculadoraDePreco;
        this.ticketRepository = ticketRepository;
        this.clock = clock;
    }

    public void pagar(Ticket ticket) {
        try {
            operadora.pagar(calculadoraDePreco.calcular(ticket, LocalDateTime.now(clock)));
            ticket.pagar(LocalDateTime.now(clock));
            ticketRepository.save(ticket);
        } catch (Exception e) {
            throw new FalhaPagamentoException(e);
        }
    }
}