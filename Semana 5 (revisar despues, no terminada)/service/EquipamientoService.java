package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.abstractfactory.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EquipamientoService {

    public Map<String, Object> procesarConfiguracionRed(String perfil, double consumoKwh) {
        SmartGridAbstractFactory factory;

        if ("INDUSTRIAL".equalsIgnoreCase(perfil)) {
            factory = new IndustrialFactory();
        } else {
            factory = new ResidencialFactory();
        }

        MonitoreoConsumo monitoreo = factory.crearMonitoreo();
        CalculadorTarifa calculador = factory.crearCalculadorTarifa();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("perfil", perfil.toUpperCase());
        respuesta.put("monitoreo", monitoreo.obtenerEstadoMonitoreo());
        respuesta.put("consumoKwh", consumoKwh);
        respuesta.put("costoTotalCOP", calculador.calcularCosto(consumoKwh));

        return respuesta;
    }
}