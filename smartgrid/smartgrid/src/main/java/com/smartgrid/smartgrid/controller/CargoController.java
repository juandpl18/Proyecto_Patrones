package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.CargoResponse;
import com.smartgrid.smartgrid.dto.CrearCargoRequest;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.CargoService;
import com.smartgrid.smartgrid.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargos")
public class CargoController {

    private final CargoService cargoService;
    private final UsuarioService usuarioService;

    public CargoController(
            CargoService cargoService, UsuarioService usuarioService) {

        this.cargoService = cargoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<CargoResponse> crear(
            Authentication authentication,
            @RequestBody CrearCargoRequest request) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cargoService.crear(solicitante, request));
    }

    @GetMapping
    public List<CargoResponse> listar() {
        return cargoService.listar();
    }

    @PutMapping("/{id}/estado")
    public CargoResponse cambiarEstado(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam boolean activo) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return cargoService.cambiarEstado(solicitante, id, activo);
    }
}