package com.viana.gerenciamento_de_estacionamento.domain.ticket.service;

import com.viana.gerenciamento_de_estacionamento.domain.ticket.Ticket;
import com.viana.gerenciamento_de_estacionamento.domain.veiculo.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraDePrecoTest {

    private CalculadoraDePreco calculadoraDePreco;
    private LocalDateTime entryTime;

    @BeforeEach
    void setUp() {
        calculadoraDePreco = new CalculadoraDePreco();
        entryTime = LocalDateTime.of(2025, 11, 25, 10, 0, 0);
    }

    @Test
    @DisplayName("Case 1: As duas primeiras horas têm o mesmo valor (Carro)")
    void deveCalcularPrecoCarroDuasPrimeirasHoras() {
        Veiculo veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        Ticket ticket = Ticket.novo(veiculo, entryTime);
        LocalDateTime exitTime = entryTime.plusHours(1).plusMinutes(30);

        BigDecimal preco = calculadoraDePreco.calcular(ticket, exitTime);

        assertEquals(new BigDecimal("20.00"), preco);
    }

    @Test
    @DisplayName("Case 2: Após duas primeiras horas, se paga valor por hora adicional (Carro)")
    void deveCalcularPrecoCarroAposDuasHorasAdicionais() {
        Veiculo veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        Ticket ticket = Ticket.novo(veiculo, entryTime);
        LocalDateTime exitTime = entryTime.plusHours(3).plusMinutes(15);

        BigDecimal preco = calculadoraDePreco.calcular(ticket, exitTime);

        assertEquals(new BigDecimal("30.00"), preco);
    }

    @Test
    @DisplayName("Case 3: Se período maior que 8 horas, preço é de diária (Carro)")
    void deveCalcularPrecoCarroDiariaMaxima() {
        Veiculo veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        Ticket ticket = Ticket.novo(veiculo, entryTime);
        LocalDateTime exitTime = entryTime.plusHours(10);

        BigDecimal preco = calculadoraDePreco.calcular(ticket, exitTime);

        assertEquals(new BigDecimal("50.00"), preco);
    }

    @Test
    @DisplayName("Case 4: As tabelas de preço variam por tipo de veículo (Moto)")
    void deveCalcularPrecoMotoComTabelasDiferentes() {
        // GIVEN
        Veiculo veiculo = new Veiculo("XYZ5678", Veiculo.TipoVeiculo.MOTO);
        Ticket ticket = Ticket.novo(veiculo, entryTime);
        LocalDateTime exitTime = entryTime.plusHours(2).plusMinutes(1);

        BigDecimal preco = calculadoraDePreco.calcular(ticket, exitTime);

        assertEquals(new BigDecimal("12.50"), preco);
    }

    @Test
    @DisplayName("Case 5: O cálculo do período é em hora com arredondamento para cima (Carro)")
    void deveArredondarParaCimaNoCalculoDoPeriodo() {
        // GIVEN
        Veiculo veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        Ticket ticket = Ticket.novo(veiculo, entryTime);
        LocalDateTime exitTime = entryTime.plusHours(4).plusMinutes(1);

        BigDecimal preco = calculadoraDePreco.calcular(ticket, exitTime);

        assertEquals(new BigDecimal("35.00"), preco);
    }

    @Test
    @DisplayName("Deve retornar 0 para entrada e saída no mesmo instante ou saída antes da entrada (considerando 1 hora min)")
    void deveRetornarPrecoMinimoParaPeriodoMuitoCurto() {
        Veiculo veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        Ticket ticket = Ticket.novo(veiculo, entryTime);
        LocalDateTime exitTimeSameTime = entryTime;
        LocalDateTime exitTimeBeforeEntry = entryTime.minusMinutes(30);

        BigDecimal precoSameTime = calculadoraDePreco.calcular(ticket, exitTimeSameTime);
        BigDecimal precoBeforeEntry = calculadoraDePreco.calcular(ticket, exitTimeBeforeEntry);

        assertEquals(new BigDecimal("10.00"), precoSameTime); // Minimum 1 hour charge
        assertEquals(new BigDecimal("10.00"), precoBeforeEntry); // Minimum 1 hour charge
    }

}