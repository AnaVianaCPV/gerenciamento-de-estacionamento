package com.viana.gerenciamento_de_estacionamento.domain.ticket.ports;

public interface FuncionarioRepository {
    boolean isFuncionario(String placa);
}
