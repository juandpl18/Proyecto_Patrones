package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.ConsumoResponse;
import com.smartgrid.smartgrid.dto.CrearMedidorRequest;
import com.smartgrid.smartgrid.dto.MedidorResponse;
import com.smartgrid.smartgrid.dto.Periodo;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.ConsumoService;
import com.smartgrid.smartgrid.service.MedidorService;
import com.smartgrid.smartgrid.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/medidores")
public class MedidorController {

    private final MedidorService medidorService;
    private final ConsumoService consumoService;
    private final UsuarioService usuarioService;

    public MedidorController(
            MedidorService medidorService,
            ConsumoService consumoService,
            UsuarioService usuarioService) {

        this.medidorService = medidorService;
        this.consumoService = consumoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<MedidorResponse> crear(
            Authentication authentication,
            @RequestBody CrearMedidorRequest request) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medidorService.crear(solicitante, request));
    }

    @GetMapping
    public List<MedidorResponse> listar() {
        return medidorService.listar();
    }

    @GetMapping("/{id}/consumo")
    public ConsumoResponse consumo(
            @PathVariable Long id,
            @RequestParam(required = false) Instant desde,
            @RequestParam(required = false) Instant hasta) {

        return consumoService.porMedidor(id, Periodo.de(desde, hasta));
    }
}