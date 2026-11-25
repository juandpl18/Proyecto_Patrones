package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.LecturaResponse;
import com.smartgrid.smartgrid.dto.Periodo;
import com.smartgrid.smartgrid.dto.RegistrarLecturaRequest;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.LecturaService;
import com.smartgrid.smartgrid.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/lecturas")
public class LecturaController {

    private final LecturaService lecturaService;
    private final UsuarioService usuarioService;

    public LecturaController(
            LecturaService lecturaService,
            UsuarioService usuarioService) {

        this.lecturaService = lecturaService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<LecturaResponse> registrar(
            Authentication authentication,
            @RequestBody RegistrarLecturaRequest request) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lecturaService.registrar(solicitante, request));
    }

    @GetMapping
    public List<LecturaResponse> listar(
            @RequestParam Long medidorId,
            @RequestParam(required = false) Instant desde,
            @RequestParam(required = false) Instant hasta) {

        return lecturaService.listar(medidorId, Periodo.de(desde, hasta));
    }
}