package com.viana.gerenciamento_de_estacionamento.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.viana.gerenciamento_de_estacionamento.domain.veiculo.Veiculo;

@Entity
@Table(name = "veiculos")
public class VeiculoEntity {

    @Id
    private String placa;

    @Enumerated(EnumType.STRING)
    private Veiculo.TipoVeiculo tipo;

    public VeiculoEntity() {
    }

    public VeiculoEntity(String placa, Veiculo.TipoVeiculo tipo) {
        this.placa = placa;
        this.tipo = tipo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Veiculo.TipoVeiculo getTipo() {
        return tipo;
    }

    public void setTipo(Veiculo.TipoVeiculo tipo) {
        this.tipo = tipo;
    }
}