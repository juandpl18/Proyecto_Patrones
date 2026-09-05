package com.smartgrid.smartgrid.abstractfactory;

public class MonitoreoResidencial implements MonitoreoConsumo {
    @Override
    public String obtenerEstadoMonitoreo() {
        return "Monitoreo Residencial: Lectura monofásica en tiempo real activa (120V/240V).";
    }
}