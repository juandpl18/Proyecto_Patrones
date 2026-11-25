package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.AlertaResponse;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.AlertaService;
import com.smartgrid.smartgrid.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;
    private final UsuarioService usuarioService;

    public AlertaController(
            AlertaService alertaService,
            UsuarioService usuarioService) {

        this.alertaService = alertaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<AlertaResponse> pendientes() {
        return alertaService.listarPendientes();
    }

    @PutMapping("/{id}/atender")
    public AlertaResponse atender(
            Authentication authentication, @PathVariable Long id) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return alertaService.atender(solicitante, id);
    }
}