# 🚀 Desafio de Testes Automatizados: SGE (Sistema de Gerenciamento de Estacionamento)

## 🎯 Objetivo
Desenvolver um **Sistema de Gerenciamento de Estacionamento (SGE)** funcional e garantir sua qualidade através de uma **cobertura robusta de testes automatizados**.

O foco deste projeto não é apenas a implementação das regras de negócio, mas principalmente a **estratégia de teste** escolhida para lidar com dependências externas críticas, como a validação de isenção de pagamento.

---

## 🏗️ Requisitos Funcionais

O SGE gerencia o fluxo completo de veículos e pagamentos, seguindo as regras abaixo:

### 1. Fluxo de Operação Básica
* **Entrada de Veículo:** Registro da entrada capturando a placa e o *timestamp* (carimbo de tempo) de entrada.
* **Saída e Cálculo:** Ao registrar a saída, o sistema calcula o tempo total de permanência.
* **Cálculo da Tarifa:** Regra de tarifa aplicada (Exemplo: R$ 5,00 na primeira hora, R$ 3,00 nas horas subsequentes, com cobrança fracionada).
* **Pagamento:** Simulação do processamento do valor total, alterando o status para **Concluído**.

### 2. O Desafio Central: Isenção de Pagamento
* O motorista pode solicitar isenção apresentando uma **Nota Fiscal Eletrônica (NF-e)** de um estabelecimento credenciado.
* O sistema deve receber e **validar** esta NF-e.
* **Regra de Ouro:** Se a validação for bem-sucedida, o valor a pagar deve ser zerado (**R$ 0,00**).

---

## ⚔️ O Desafio de Testabilidade
A validação da NF-e representa o ponto de maior complexidade do projeto, pois a confirmação da validade reside em um **Serviço de Validação Externo**.

A missão deste projeto é demonstrar a melhor estratégia de integração e testes:

* **Testes Unitários:** Cobrir a lógica de cálculo de tarifa e funções internas.
* **Testes de Integração:** Garantir a persistência de dados e, crucialmente, a interação com o módulo de validação de isenção (uso de Mocks/Stubs).
* **Testes E2E (End-to-End):** Simular o fluxo completo: `Entrada` -> `Validação` -> `Saída/Pagamento`.

---

## 📋 Histórias de Usuário e Cenários de Teste

Abaixo estão os cenários críticos cobertos pelos testes automatizados:

| Cenário de Teste | Descrição | Resultado Esperado |
| :--- | :--- | :--- |
| **Cálculo Base** | Veículo permanece por 2 horas e 30 minutos sem isenção. | O cálculo da tarifa deve estar correto conforme a tabela de preços. |
| **Isenção Válida** | O veículo apresenta um comprovante de isenção (NF-e) válido via API/Token. | O valor a pagar deve ser **R$ 0,00**. |
| **Comprovante Expirado** | O comprovante/NF-e possui formato correto, mas está fora da validade (ex: mais de 24h). | A isenção deve ser negada e o **valor total** deve ser cobrado. |
| **Comprovante Inválido** | O comprovante está com formato incorreto ou assinatura inválida. | O sistema deve **rejeitar** a isenção e cobrar o valor total. |
| **Pagamento Completo** | O veículo não tem isenção e realiza o pagamento. | O pagamento deve ser processado com sucesso. |

---

## 🛠️ Tecnologias Utilizadas
* **Java**
* **Spring Boot**
* **JUnit / Mockito** (para testes unitários e mocks)
* **Banco de Dados** (H2/PostgreSQL)

---
*Este projeto faz parte de um desafio técnico para demonstrar competências em qualidade de software e testes automatizados.*