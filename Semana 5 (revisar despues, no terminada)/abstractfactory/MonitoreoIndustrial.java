package com.smartgrid.smartgrid.abstractfactory;

public class MonitoreoIndustrial implements MonitoreoConsumo {
    @Override
    public String obtenerEstadoMonitoreo() {
        return "Monitoreo Industrial: Lectura trifásica de alta tensión y medición de factor de potencia activa.";
    }
}