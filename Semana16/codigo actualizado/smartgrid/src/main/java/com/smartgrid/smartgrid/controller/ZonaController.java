package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.CrearZonaRequest;
import com.smartgrid.smartgrid.dto.NodoConsumoResponse;
import com.smartgrid.smartgrid.dto.Periodo;
import com.smartgrid.smartgrid.dto.ZonaResponse;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.ConsumoService;
import com.smartgrid.smartgrid.service.UsuarioService;
import com.smartgrid.smartgrid.service.ZonaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/zonas")
public class ZonaController {

    private final ZonaService zonaService;
    private final ConsumoService consumoService;
    private final UsuarioService usuarioService;

    public ZonaController(
            ZonaService zonaService,
            ConsumoService consumoService,
            UsuarioService usuarioService) {

        this.zonaService = zonaService;
        this.consumoService = consumoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<ZonaResponse> crear(
            Authentication authentication,
            @RequestBody CrearZonaRequest request) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(zonaService.crear(solicitante, request));
    }

    @GetMapping
    public List<ZonaResponse> listar() {
        return zonaService.listar();
    }

    /** Árbol de la red (Composite) con el consumo acumulado por nodo. */
    @GetMapping("/arbol")
    public List<NodoConsumoResponse> arbol(
            @RequestParam(required = false) Instant desde,
            @RequestParam(required = false) Instant hasta) {

        return consumoService.arbol(Periodo.de(desde, hasta));
    }
}