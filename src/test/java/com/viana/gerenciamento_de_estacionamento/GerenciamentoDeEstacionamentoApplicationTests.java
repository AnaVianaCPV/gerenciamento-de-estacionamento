package com.viana.gerenciamento_de_estacionamento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class GerenciamentoDeEstacionamentoApplicationTests {

	@Test
	void contextLoads() {
	}

}
