package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.service.EquipamientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/equipamiento")
public class EquipamientoController {

    private final EquipamientoService equipamientoService;

    public EquipamientoController(EquipamientoService equipamientoService) {
        this.equipamientoService = equipamientoService;
    }

    @GetMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarEquipamiento(
            @RequestParam(defaultValue = "RESIDENCIAL") String perfil,
            @RequestParam(defaultValue = "100") double consumoKwh) {
        
        Map<String, Object> resultado = equipamientoService.procesarConfiguracionRed(perfil, consumoKwh);
        return ResponseEntity.ok(resultado);
    }
}