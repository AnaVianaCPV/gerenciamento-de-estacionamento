package com.viana.gerenciamento_de_estacionamento.api;


import com.viana.gerenciamento_de_estacionamento.domain.ticket.Ticket;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.ports.TicketRepository;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.service.EmissaoService;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.service.IsencaoService;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.service.PagamentoService;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.service.SaidaService;
import com.viana.gerenciamento_de_estacionamento.domain.veiculo.Veiculo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final EmissaoService emissaoService;
    private final PagamentoService pagamentoService;
    private final SaidaService saidaService;
    private final IsencaoService isencaoService;
    private final TicketRepository ticketRepository;

    public TicketController(EmissaoService emissaoService, PagamentoService pagamentoService, SaidaService saidaService, IsencaoService isencaoService, TicketRepository ticketRepository) {
        this.emissaoService = emissaoService;
        this.pagamentoService = pagamentoService;
        this.saidaService = saidaService;
        this.isencaoService = isencaoService;
        this.ticketRepository = ticketRepository;
    }

    @PostMapping
    public ResponseEntity<Ticket> emitirTicket(@RequestBody Veiculo veiculo) {
        Ticket ticketEmitido = emissaoService.emitir(veiculo);
        return ResponseEntity.ok(ticketEmitido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable String id) {
        return ticketRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<Void> pagarTicket(@PathVariable String id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        pagamentoService.pagar(ticket);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/exit")
    public ResponseEntity<Void> processarSaida(@PathVariable String id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        saidaService.processarSaida(ticket);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/exempt-by-receipt")
    public ResponseEntity<Void> isentarPorComprovanteDeCompra(@PathVariable String id, @RequestBody String comprovante) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        isencaoService.isentarPorComprovanteDeCompra(ticket, comprovante);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/exempt-employee")
    public ResponseEntity<Void> isentarFuncionario(@PathVariable String id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        isencaoService.isentarFuncionario(ticket);
        return ResponseEntity.ok().build();
    }

}