package com.viana.gerenciamento_de_estacionamento.domain.ticket.service;


import com.viana.gerenciamento_de_estacionamento.domain.ticket.Status;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.Ticket;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.ports.TicketRepository;
import com.viana.gerenciamento_de_estacionamento.domain.veiculo.Veiculo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.Instant;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaidaServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private java.time.Clock clock;

    @InjectMocks
    private SaidaService saidaService;

    private final LocalDateTime FIXED_ENTRY_TIME = LocalDateTime.of(2025, 11, 25, 10, 0, 0);

    private Ticket ticket;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {

        veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        ticket = Ticket.novo(veiculo, FIXED_ENTRY_TIME);
    }


    private void setupClock(LocalDateTime simulatedTime) {
        Instant instant = simulatedTime.atZone(ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    @DisplayName("Deve alterar o status para FINALIZADO se status for ISENTO")
    void deveFinalizarTicketIsento() {
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.plusMinutes(60);
        setupClock(saidaTime);
        ticket.isentar(Status.ISENTO);

        saidaService.processarSaida(ticket);

        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(saidaTime, ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Deve alterar o status para FINALIZADO se status for PAGO e saída em até 15 minutos do pagamento")
    void deveFinalizarTicketPagoDentroDoLimite() {
        LocalDateTime paymentTime = FIXED_ENTRY_TIME.plusHours(1);
        ticket.pagar(paymentTime);
        LocalDateTime saidaTime = paymentTime.plusMinutes(14);
        setupClock(saidaTime);

        saidaService.processarSaida(ticket);

        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(saidaTime, ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Não deve alterar o status para FINALIZADO se hora atual for fora do horário de funcionamento (exceto funcionário)")
    void naoDeveFinalizarForaDoHorarioDeFuncionamento() {

        LocalDateTime saidaTime = LocalDateTime.of(2025, 11, 25, 22, 1, 0);
        setupClock(saidaTime); // Configura o Clock para 22:01

        ticket.isentar(Status.ISENTO);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
        });

        assertEquals("Ticket não pode ser finalizado fora do horário de funcionamento.", thrown.getMessage());
        assertNotEquals(Status.FINALIZADO, ticket.getStatus());
        assertNull(ticket.getSaida());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Deve alterar o status para FINALIZADO se status for ISENTO_FUNCIONARIO, mesmo fora do horário")
    void deveFinalizarTicketIsentoFuncionarioMesmoForaDoHorario() {
        ticket.isentar(Status.ISENTO_FUNCIONARIO);
        LocalDateTime saidaTime = LocalDateTime.of(2025, 11, 25, 23, 0, 0);
        setupClock(saidaTime);

        saidaService.processarSaida(ticket);

        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(saidaTime, ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Deve finalizar ticket PENDENTE se permanência menor que 15 minutos (tolerância)")
    void deveFinalizarTicketPendenteSeDentroTolerancia() {
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.plusMinutes(14);
        setupClock(saidaTime);

        saidaService.processarSaida(ticket);

        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(saidaTime, ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Não deve finalizar ticket PENDENTE se permanência maior ou igual a 15 minutos (fora tolerância)")
    void naoDeveFinalizarTicketPendenteSeForaTolerancia() {
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.plusMinutes(15);
        setupClock(saidaTime);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
        });

        assertEquals("Ticket com status PENDENTE não pode ser finalizado.", thrown.getMessage());
        assertEquals(Status.PENDENTE, ticket.getStatus());
        assertNull(ticket.getSaida());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void deveExpirarPagamentoSeExcederLimite() {

        LocalDateTime paymentTime = FIXED_ENTRY_TIME.plusHours(1);
        ticket.pagar(paymentTime);
        LocalDateTime saidaTime = paymentTime.plusMinutes(16);
        setupClock(saidaTime);


        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
        });

        assertEquals("Ticket não pode ser finalizado após o limite de tolerância. Necessário novo pagamento.", thrown.getMessage());
        assertEquals(Status.TOLERANCIA_APOS_PAGAMENTO_EXPIRADO, ticket.getStatus());
        assertNull(ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void deveExpirarIsencaoSeExcederLimite() {
        ticket.isentar(Status.ISENTO);
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.plusMinutes(121);
        setupClock(saidaTime);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
        });

        assertEquals("Ticket não pode ser finalizado após o limite de tolerância. Necessário novo pagamento.", thrown.getMessage());
        assertEquals(Status.TOLERANCIA_APOS_ISENCAO_EXPIRADO, ticket.getStatus());
        assertNull(ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Não deve permitir saída se data e hora de saída for anterior à entrada")
    void naoDevePermitirSaidaAnteriorAEntrada() {
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.minusMinutes(1);
        setupClock(saidaTime);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
        });

        assertEquals("Ticket não pode ser finalizado antes da entrada.", thrown.getMessage());
        assertNull(ticket.getSaida());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Não deve finalizar ticket com status inválido para saída")
    void naoDeveFinalizarTicketComStatusInvalido() {

        LocalDateTime saidaTime = FIXED_ENTRY_TIME.plusMinutes(20);
        setupClock(saidaTime);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
        });

        assertEquals("Ticket com status PENDENTE não pode ser finalizado.", thrown.getMessage());
        assertEquals(Status.PENDENTE, ticket.getStatus());
        assertNull(ticket.getSaida());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}