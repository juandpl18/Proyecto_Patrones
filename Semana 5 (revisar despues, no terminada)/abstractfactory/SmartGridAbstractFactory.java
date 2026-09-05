package com.smartgrid.smartgrid.abstractfactory;

public interface SmartGridAbstractFactory {
    MonitoreoConsumo crearMonitoreo();
    CalculadorTarifa crearCalculadorTarifa();
}