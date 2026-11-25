package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.PlantaResponse;
import com.smartgrid.smartgrid.model.PlantaGeneracion;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.PlantaEnergiaProxy;
import com.smartgrid.smartgrid.service.PlantaGeneracionService;
import com.smartgrid.smartgrid.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planta")
public class PlantaEnergiaController {

    private final PlantaEnergiaProxy plantaEnergiaProxy;
    private final PlantaGeneracionService plantaGeneracionService;
    private final UsuarioService usuarioService;

    public PlantaEnergiaController(
            PlantaEnergiaProxy plantaEnergiaProxy,
            PlantaGeneracionService plantaGeneracionService,
            UsuarioService usuarioService) {

        this.plantaEnergiaProxy = plantaEnergiaProxy;
        this.plantaGeneracionService = plantaGeneracionService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/crear")
    public ResponseEntity<PlantaResponse> crearPlanta(
            Authentication authentication,
            @RequestParam String nombre,
            @RequestParam String fuente,
            @RequestParam(required = false) Double capacidad,
            @RequestParam(required = false) Boolean bateria,
            @RequestParam(required = false) String ubicacion) {

        Usuario usuario =
                usuarioService.obtenerAutenticado(authentication.getName());

        PlantaGeneracion planta = plantaEnergiaProxy.construirPlanta(
                usuario, nombre, fuente, capacidad, bateria, ubicacion);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PlantaResponse.desde(planta));
    }

    @GetMapping
    public List<PlantaResponse> listar() {
        return plantaGeneracionService.listar().stream()
                .map(PlantaResponse::desde)
                .toList();
    }
}