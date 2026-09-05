package com.smartgrid.smartgrid.abstractfactory;

public class CalculadorTarifaResidencial implements CalculadorTarifa {
    @Override
    public double calcularCosto(double consumoKwh) {
        double tarifaBaseResidencial = 550.0; // COP por kWh
        return consumoKwh * tarifaBaseResidencial;
    }
}