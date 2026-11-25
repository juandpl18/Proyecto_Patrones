package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.ClienteResponse;
import com.smartgrid.smartgrid.dto.ConsumoResponse;
import com.smartgrid.smartgrid.dto.CrearClienteRequest;
import com.smartgrid.smartgrid.dto.Periodo;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.ClienteService;
import com.smartgrid.smartgrid.service.ConsumoService;
import com.smartgrid.smartgrid.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ConsumoService consumoService;
    private final UsuarioService usuarioService;

    public ClienteController(
            ClienteService clienteService,
            ConsumoService consumoService,
            UsuarioService usuarioService) {

        this.clienteService = clienteService;
        this.consumoService = consumoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(
            Authentication authentication,
            @RequestBody CrearClienteRequest request) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.crear(solicitante, request));
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}/consumo")
    public ConsumoResponse consumo(
            @PathVariable Long id,
            @RequestParam(required = false) Instant desde,
            @RequestParam(required = false) Instant hasta) {

        return consumoService.porCliente(id, Periodo.de(desde, hasta));
    }
}