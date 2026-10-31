package com.smartgrid.smartgrid.chain;

import org.springframework.stereotype.Component;

@Component
public class PlantaEnergiaValidacionChain {

    private final ValidacionHandler primeraValidacion;

    public PlantaEnergiaValidacionChain(
            NombreValidacion nombreValidacion,
            FuenteEnergiaValidacion fuenteValidacion,
            CapacidadValidacion capacidadValidacion) {

        nombreValidacion.establecerSiguiente(fuenteValidacion);
        fuenteValidacion.establecerSiguiente(capacidadValidacion);

        this.primeraValidacion = nombreValidacion;
    }

    public void validar(String nombre, String fuente, Double capacidad) {
        primeraValidacion.validar(nombre, fuente, capacidad);
    }
}