package com.viana.gerenciamento_de_estacionamento.domain.ticket.ports;

import java.math.BigDecimal;

public interface OperadoraPagamento {
    void pagar(BigDecimal valor);
}