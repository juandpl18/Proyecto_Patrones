package com.smartgrid.smartgrid.abstractfactory;

public class IndustrialFactory implements SmartGridAbstractFactory {
    @Override
    public MonitoreoConsumo crearMonitoreo() {
        return new MonitoreoIndustrial();
    }

    @Override
    public CalculadorTarifa crearCalculadorTarifa() {
        return new CalculadorTarifaIndustrial();
    }
}