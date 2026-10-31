package com.smartgrid.smartgrid.command;

import com.smartgrid.smartgrid.builder.PlantaEnergia;
import com.smartgrid.smartgrid.service.PlantaEnergiaService;

public class CrearPlantaCommand implements Comando {

    private final PlantaEnergiaService plantaEnergiaService;

    private final String nombre;
    private final String fuente;
    private final Double capacidad;
    private final Boolean bateria;
    private final String ubicacion;

    public CrearPlantaCommand(
            PlantaEnergiaService plantaEnergiaService,
            String nombre,
            String fuente,
            Double capacidad,
            Boolean bateria,
            String ubicacion) {

        this.plantaEnergiaService = plantaEnergiaService;
        this.nombre = nombre;
        this.fuente = fuente;
        this.capacidad = capacidad;
        this.bateria = bateria;
        this.ubicacion = ubicacion;
    }

    @Override
    public PlantaEnergia ejecutar() {

        return plantaEnergiaService.construirPlanta(
                nombre,
                fuente,
                capacidad,
                bateria,
                ubicacion
        );
    }
}