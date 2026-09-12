package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.builder.PlantaEnergia;
import com.smartgrid.smartgrid.service.PlantaEnergiaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/planta")
@CrossOrigin(origins = "*")
public class PlantaEnergiaController {

    private final PlantaEnergiaService plantaEnergiaService;

    public PlantaEnergiaController(PlantaEnergiaService plantaEnergiaService) {
        this.plantaEnergiaService = plantaEnergiaService;
    }

    @PostMapping("/crear")
    public PlantaEnergia crearPlanta(
            @RequestParam String nombre,
            @RequestParam String fuente,
            @RequestParam(required = false) Double capacidad,
            @RequestParam(required = false) Boolean bateria,
            @RequestParam(required = false) String ubicacion) {

        return plantaEnergiaService.construirPlanta(nombre, fuente, capacidad, bateria, ubicacion);
    }
}