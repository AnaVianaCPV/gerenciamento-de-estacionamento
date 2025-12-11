package com.viana.gerenciamento_de_estacionamento.domain.ticket.service;

import com.viana.gerenciamento_de_estacionamento.domain.ticket.Status;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.Ticket;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.ports.FuncionarioRepository;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.ports.TicketRepository;
import com.viana.gerenciamento_de_estacionamento.domain.ticket.ports.ValidadorComprovante;
import com.viana.gerenciamento_de_estacionamento.domain.veiculo.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IsencaoServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private FuncionarioRepository funcionarioRepository;
    @Mock
    private ValidadorComprovante validadorComprovante;

    @InjectMocks
    private IsencaoService isencaoService;

    private Ticket ticket;
    private Veiculo veiculo;
    private final LocalDateTime FIXED_ENTRY_TIME = LocalDateTime.of(2025, 11, 25, 10, 0, 0);

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        ticket = Ticket.novo(veiculo, FIXED_ENTRY_TIME);
    }

    @Test
    @DisplayName("Deve considerar isento se comprovante de compra válido")
    void deveIsentarSeComprovanteDeCompraValido() {
        String comprovanteValido = "comprovante123";
        when(validadorComprovante.validar(comprovanteValido)).thenReturn(true);

        isencaoService.isentarPorComprovanteDeCompra(ticket, comprovanteValido);

        assertEquals(Status.ISENTO, ticket.getStatus());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Não deve isentar se comprovante de compra inválido")
    void naoDeveIsentarSeComprovanteDeCompraInvalido() {
        String comprovanteInvalido = "comprovanteInvalido";
        when(validadorComprovante.validar(comprovanteInvalido)).thenReturn(false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            isencaoService.isentarPorComprovanteDeCompra(ticket, comprovanteInvalido);
        });
        assertEquals("Comprovante inválido.", thrown.getMessage());
        assertEquals(Status.PENDENTE, ticket.getStatus());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Deve considerar isento se placa cadastrada de funcionário")
    void deveIsentarSePlacaDeFuncionario() {
        Veiculo veiculoFuncionario = new Veiculo("FUN0001", Veiculo.TipoVeiculo.CARRO);
        Ticket ticketFuncionario = Ticket.novo(veiculoFuncionario, FIXED_ENTRY_TIME);
        when(funcionarioRepository.isFuncionario(veiculoFuncionario.getPlaca())).thenReturn(true);

        isencaoService.isentarFuncionario(ticketFuncionario);

        assertEquals(Status.ISENTO_FUNCIONARIO, ticketFuncionario.getStatus());
        verify(ticketRepository, times(1)).save(ticketFuncionario);
    }

    @Test
    @DisplayName("Não deve isentar se placa não pertence a um funcionário")
    void naoDeveIsentarSePlacaNaoDeFuncionario() {
        Veiculo veiculoNaoFuncionario = new Veiculo("NUL0001", Veiculo.TipoVeiculo.CARRO);
        Ticket ticketNaoFuncionario = Ticket.novo(veiculoNaoFuncionario, FIXED_ENTRY_TIME);
        when(funcionarioRepository.isFuncionario(veiculoNaoFuncionario.getPlaca())).thenReturn(false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            isencaoService.isentarFuncionario(ticketNaoFuncionario);
        });
        assertEquals("Placa não pertence a um funcionário.", thrown.getMessage());
        assertEquals(Status.PENDENTE, ticketNaoFuncionario.getStatus());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}