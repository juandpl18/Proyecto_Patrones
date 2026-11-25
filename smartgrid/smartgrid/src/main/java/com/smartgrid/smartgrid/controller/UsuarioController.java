package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.CrearUsuarioRequest;
import com.smartgrid.smartgrid.dto.UsuarioResponse;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            Authentication authentication,
            @RequestBody CrearUsuarioRequest request) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        Usuario creado = usuarioService.crear(solicitante, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.desde(creado));
    }

    @GetMapping
    public List<UsuarioResponse> listar(Authentication authentication) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return usuarioService.listar(solicitante).stream()
                .map(UsuarioResponse::desde)
                .toList();
    }

    @PutMapping("/{id}/estado")
    public UsuarioResponse cambiarEstado(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam boolean activo) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return UsuarioResponse.desde(
                usuarioService.cambiarEstado(solicitante, id, activo));
    }
}