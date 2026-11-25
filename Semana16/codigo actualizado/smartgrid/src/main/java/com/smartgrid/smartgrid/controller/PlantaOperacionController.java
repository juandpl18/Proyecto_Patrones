package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.CambioEstadoResponse;
import com.smartgrid.smartgrid.dto.PlantaDetalleResponse;
import com.smartgrid.smartgrid.dto.ProduccionResponse;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.PlantaOperacionService;
import com.smartgrid.smartgrid.service.ProduccionService;
import com.smartgrid.smartgrid.service.UsuarioService;
import com.smartgrid.smartgrid.strategy.CondicionesProduccion;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planta/{id}")
public class PlantaOperacionController {

    private final PlantaOperacionService operacionService;
    private final ProduccionService produccionService;
    private final UsuarioService usuarioService;

    public PlantaOperacionController(
            PlantaOperacionService operacionService,
            ProduccionService produccionService,
            UsuarioService usuarioService) {

        this.operacionService = operacionService;
        this.produccionService = produccionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public PlantaDetalleResponse detalle(@PathVariable Long id) {
        return operacionService.obtener(id);
    }

    @PutMapping("/estado")
    public PlantaDetalleResponse cambiarEstado(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) String motivo) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return operacionService.cambiarEstado(solicitante, id, estado, motivo);
    }

    @GetMapping("/historial-estados")
    public List<CambioEstadoResponse> historial(@PathVariable Long id) {
        return operacionService.historial(id);
    }

    @PutMapping("/zona")
    public PlantaDetalleResponse asignarZona(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam Long zonaId) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return operacionService.asignarZona(solicitante, id, zonaId);
    }

    @GetMapping("/produccion")
    public ProduccionResponse produccion(
            @PathVariable Long id,
            @RequestParam(required = false) Double irradianciaWm2,
            @RequestParam(required = false) Double vientoMs,
            @RequestParam(required = false) Double factorDisponibilidad) {

        return produccionService.estimar(id, CondicionesProduccion.de(
                irradianciaWm2, vientoMs, factorDisponibilidad));
    }
}