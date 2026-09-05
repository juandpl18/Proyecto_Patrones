package com.smartgrid.smartgrid.abstractfactory;

public class CalculadorTarifaIndustrial implements CalculadorTarifa {
    @Override
    public double calcularCosto(double consumoKwh) {
        double tarifaBaseIndustrial = 850.0; // COP por kWh
        double cargoDemandaPico = 150000.0; // Cargo fijo por alta tensión
        return (consumoKwh * tarifaBaseIndustrial) + cargoDemandaPico;
    }
}
