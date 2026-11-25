package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.FacturaResponse;
import com.smartgrid.smartgrid.dto.LoteResponse;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.FacturaLoteService;
import com.smartgrid.smartgrid.service.FacturaService;
import com.smartgrid.smartgrid.service.UsuarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;
    private final FacturaLoteService loteService;
    private final UsuarioService usuarioService;

    public FacturaController(
            FacturaService facturaService,
            FacturaLoteService loteService,
            UsuarioService usuarioService) {

        this.facturaService = facturaService;
        this.loteService = loteService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/emitir")
    public ResponseEntity<FacturaResponse> emitir(
            Authentication authentication,
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta,
            @RequestParam(defaultValue = "LECTURAS") String modo) {

        Usuario solicitante = usuario(authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facturaService.emitir(
                        solicitante, clienteId, desde, hasta, modo));
    }

    @PostMapping("/emitir-lote")
    public LoteResponse emitirLote(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta,
            @RequestParam(defaultValue = "LECTURAS") String modo) {

        return loteService.emitirLote(
                usuario(authentication), desde, hasta, modo);
    }

    /** Calcula sin guardar. Con tarifaId permite comparar otra tarifa. */
    @GetMapping("/simular")
    public FacturaResponse simular(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta,
            @RequestParam(defaultValue = "LECTURAS") String modo,
            @RequestParam(required = false) Long tarifaId) {

        return facturaService.simular(clienteId, desde, hasta, modo, tarifaId);
    }

    @GetMapping
    public List<FacturaResponse> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String estado) {

        return facturaService.listar(clienteId, estado);
    }

    @GetMapping("/{id}")
    public FacturaResponse obtener(@PathVariable Long id) {
        return facturaService.obtener(id);
    }

    @PutMapping("/{id}/pagar")
    public FacturaResponse pagar(
            Authentication authentication, @PathVariable Long id) {

        return facturaService.pagar(usuario(authentication), id);
    }

    @PutMapping("/{id}/anular")
    public FacturaResponse anular(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {

        return facturaService.anular(usuario(authentication), id, motivo);
    }

    @PostMapping("/marcar-vencidas")
    public Map<String, Integer> marcarVencidas(Authentication authentication) {
        return facturaService.marcarVencidas(usuario(authentication));
    }

    private Usuario usuario(Authentication authentication) {
        return usuarioService.obtenerAutenticado(authentication.getName());
    }
}