package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.BalanceResponse;
import com.smartgrid.smartgrid.dto.EjecucionResponse;
import com.smartgrid.smartgrid.dto.EstrategiaResponse;
import com.smartgrid.smartgrid.dto.EventoDemandaResponse;
import com.smartgrid.smartgrid.dto.PlanBalanceoResponse;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.BalanceService;
import com.smartgrid.smartgrid.service.BalanceoService;
import com.smartgrid.smartgrid.service.UsuarioService;
import com.smartgrid.smartgrid.strategy.CondicionesProduccion;
import com.smartgrid.smartgrid.strategy.balanceo.BalanceoStrategyRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    private final BalanceService balanceService;
    private final BalanceoService balanceoService;
    private final BalanceoStrategyRegistry estrategias;
    private final UsuarioService usuarioService;

    public BalanceController(
            BalanceService balanceService,
            BalanceoService balanceoService,
            BalanceoStrategyRegistry estrategias,
            UsuarioService usuarioService) {

        this.balanceService = balanceService;
        this.balanceoService = balanceoService;
        this.estrategias = estrategias;
        this.usuarioService = usuarioService;
    }

    /** Situación de las zonas hijas de zonaId (demanda vs. generación). */
    @GetMapping
    public BalanceResponse diagnostico(
            @RequestParam Long zonaId,
            @RequestParam(required = false) Instant hasta,
            @RequestParam(required = false) Double irradianciaWm2,
            @RequestParam(required = false) Double vientoMs,
            @RequestParam(required = false) Double factorDisponibilidad) {

        return balanceService.diagnosticar(zonaId, hasta,
                CondicionesProduccion.de(
                        irradianciaWm2, vientoMs, factorDisponibilidad));
    }

    @GetMapping("/estrategias")
    public List<EstrategiaResponse> estrategias() {
        return estrategias.todas().stream()
                .map(e -> new EstrategiaResponse(e.nombre(), e.descripcion()))
                .toList();
    }

    /** Simula el balanceo: no modifica nada. */
    @GetMapping("/plan")
    public PlanBalanceoResponse plan(
            @RequestParam Long zonaId,
            @RequestParam(defaultValue = "PROPORCIONAL") String estrategia,
            @RequestParam(required = false) Instant hasta,
            @RequestParam(required = false) Double irradianciaWm2,
            @RequestParam(required = false) Double vientoMs,
            @RequestParam(required = false) Double factorDisponibilidad) {

        return balanceoService.planificar(zonaId, estrategia, hasta,
                CondicionesProduccion.de(
                        irradianciaWm2, vientoMs, factorDisponibilidad));
    }

    @PostMapping("/ejecutar")
    public ResponseEntity<EjecucionResponse> ejecutar(
            Authentication authentication,
            @RequestParam Long zonaId,
            @RequestParam(defaultValue = "PROPORCIONAL") String estrategia,
            @RequestParam(required = false) Instant hasta,
            @RequestParam(required = false) Double irradianciaWm2,
            @RequestParam(required = false) Double vientoMs,
            @RequestParam(required = false) Double factorDisponibilidad) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(balanceoService.ejecutar(solicitante, zonaId,
                        estrategia, hasta,
                        CondicionesProduccion.de(irradianciaWm2, vientoMs,
                                factorDisponibilidad)));
    }

    @PostMapping("/ejecuciones/{id}/revertir")
    public EjecucionResponse revertir(
            Authentication authentication, @PathVariable Long id) {

        Usuario solicitante =
                usuarioService.obtenerAutenticado(authentication.getName());

        return balanceoService.revertir(solicitante, id);
    }

    @GetMapping("/ejecuciones")
    public List<EjecucionResponse> ejecuciones(
            @RequestParam(required = false) Long zonaId) {

        return balanceoService.listar(zonaId);
    }

    @GetMapping("/ejecuciones/{id}")
    public EjecucionResponse ejecucion(@PathVariable Long id) {
        return balanceoService.obtener(id);
    }

    /** Historial de demanda/generación y acciones registradas de una zona. */
    @GetMapping("/eventos")
    public List<EventoDemandaResponse> eventos(@RequestParam Long zonaId) {
        return balanceoService.eventos(zonaId);
    }
}