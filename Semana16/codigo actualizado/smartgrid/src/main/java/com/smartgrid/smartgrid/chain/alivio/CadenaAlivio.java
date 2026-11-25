package com.smartgrid.smartgrid.chain.alivio;

import org.springframework.stereotype.Component;

import java.util.List;

/** Arma la cadena: tolerancia -> plantas en mantenimiento -> deslastre. */
@Component
public class CadenaAlivio {

    private final ReglaAlivio inicio;

    public CadenaAlivio() {
        ReglaAlivio tolerancia = new ToleranciaRegla();
        tolerancia
                .setSiguiente(new PlantasMantenimientoRegla())
                .setSiguiente(new DeslastreRegla());
        this.inicio = tolerancia;
    }

    public AlivioContexto evaluar(
            Long zonaId,
            String zonaNombre,
            double deficitKw,
            double demandaKw,
            List<PlantaRecuperable> plantasRecuperables) {

        AlivioContexto contexto = new AlivioContexto(
                zonaId, zonaNombre, deficitKw, demandaKw, plantasRecuperables);

        inicio.aplicar(contexto);
        return contexto;
    }
}