package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.GeneracionResumenResponse;
import com.smartgrid.smartgrid.service.GeneracionResumenService;
import com.smartgrid.smartgrid.strategy.CondicionesProduccion;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generacion")
public class GeneracionController {

    private final GeneracionResumenService resumenService;

    public GeneracionController(GeneracionResumenService resumenService) {
        this.resumenService = resumenService;
    }

    @GetMapping("/resumen")
    public GeneracionResumenResponse resumen(
            @RequestParam(required = false) Double irradianciaWm2,
            @RequestParam(required = false) Double vientoMs,
            @RequestParam(required = false) Double factorDisponibilidad) {

        return resumenService.resumir(CondicionesProduccion.de(
                irradianciaWm2, vientoMs, factorDisponibilidad));
    }
}