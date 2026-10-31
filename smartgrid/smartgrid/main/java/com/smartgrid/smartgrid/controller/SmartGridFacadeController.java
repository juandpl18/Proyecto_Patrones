
package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.facade.SmartGridFacade;
import org.springframework.web.bind.annotation.*;
import com.smartgrid.smartgrid.dto.PanelResumenResponse;

@RestController
@RequestMapping("/api/facade")
@CrossOrigin(origins = "*")
public class SmartGridFacadeController {

    private final SmartGridFacade smartGridFacade;

    public SmartGridFacadeController(SmartGridFacade smartGridFacade) {
        this.smartGridFacade = smartGridFacade;
    }

    @GetMapping("/resumen")
    public String obtenerResumen() {
        return smartGridFacade.obtenerResumen();
    }

    @GetMapping("/panel")
    public String obtenerPanel(
            @RequestParam(defaultValue = "RESIDENCIAL") String tipo,
            @RequestParam(defaultValue = "350") double consumo) {

        return smartGridFacade.obtenerPanel(tipo, consumo);
    }

    @GetMapping("/panel-json")
    public PanelResumenResponse obtenerPanelVisual(
            @RequestParam(defaultValue = "RESIDENCIAL") String tipo,
            @RequestParam(defaultValue = "350") double consumo) {

        return smartGridFacade.obtenerPanelVisual(tipo, consumo);
    }
}