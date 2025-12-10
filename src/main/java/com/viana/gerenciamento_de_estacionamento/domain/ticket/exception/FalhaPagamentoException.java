package com.viana.gerenciamento_de_estacionamento.domain.ticket.exception;

public class FalhaPagamentoException extends RuntimeException {
    public FalhaPagamentoException(Throwable cause) {
        super("Erro ao processar o pagamento.", cause);
    }
}