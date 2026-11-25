package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.CrearTarifaRequest;
import com.smartgrid.smartgrid.dto.TarifaResponse;
import com.smartgrid.smartgrid.model.TipoCliente;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.TarifaService;
import com.smartgrid.smartgrid.service.UsuarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    private final TarifaService tarifaService;
    private final UsuarioService usuarioService;

    public TarifaController(
            TarifaService tarifaService, UsuarioService usuarioService) {

        this.tarifaService = tarifaService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<TarifaResponse> crear(
            Authentication authentication,
            @RequestBody CrearTarifaRequest request) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tarifaService.crear(solicitante, request));
    }

    @GetMapping
    public List<TarifaResponse> listar() {
        return tarifaService.listar();
    }

    /** Tarifa vigente de un tipo de cliente (por defecto, hoy). */
    @GetMapping("/vigente")
    public TarifaResponse vigente(
            @RequestParam TipoCliente tipoCliente,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return tarifaService.vigenteResponse(
                tipoCliente, fecha != null ? fecha : LocalDate.now());
    }
}