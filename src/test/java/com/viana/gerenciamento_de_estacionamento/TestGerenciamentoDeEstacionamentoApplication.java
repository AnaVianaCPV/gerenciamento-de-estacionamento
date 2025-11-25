package com.viana.gerenciamento_de_estacionamento;

import org.springframework.boot.SpringApplication;

public class TestGerenciamentoDeEstacionamentoApplication {

	public static void main(String[] args) {
		SpringApplication.from(GerenciamentoDeEstacionamentoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
