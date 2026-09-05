package com.smartgrid.smartgrid.abstractfactory;

public class ResidencialFactory implements SmartGridAbstractFactory {
    @Override
    public MonitoreoConsumo crearMonitoreo() {
        return new MonitoreoResidencial();
    }

    @Override
    public CalculadorTarifa crearCalculadorTarifa() {
        return new CalculadorTarifaResidencial();
    }
}