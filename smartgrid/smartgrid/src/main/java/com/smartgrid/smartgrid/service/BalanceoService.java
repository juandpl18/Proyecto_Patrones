package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.balanceo.AnalisisBalance;
import com.smartgrid.smartgrid.balanceo.BalanceoPlanner;
import com.smartgrid.smartgrid.balanceo.PlanCompleto;
import com.smartgrid.smartgrid.balanceo.ZonaAnalizada;
import com.smartgrid.smartgrid.command.balanceo.AccionBalanceoCommand;
import com.smartgrid.smartgrid.command.balanceo.AccionBalanceoFactory;
import com.smartgrid.smartgrid.command.balanceo.BalanceoInvoker;
import com.smartgrid.smartgrid.dto.EjecucionResponse;
import com.smartgrid.smartgrid.dto.EventoDemandaResponse;
import com.smartgrid.smartgrid.dto.PlanBalanceoResponse;
import com.smartgrid.smartgrid.model.AccionBalanceo;
import com.smartgrid.smartgrid.model.EjecucionBalanceo;
import com.smartgrid.smartgrid.model.EstadoEjecucion;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.TipoAccionBalanceo;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.AccionBalanceoRepository;
import com.smartgrid.smartgrid.repository.EjecucionBalanceoRepository;
import com.smartgrid.smartgrid.repository.EventoDemandaRepository;
import com.smartgrid.smartgrid.strategy.CondicionesProduccion;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BalanceoService {

    private final BalanceService balanceService;
    private final BalanceoPlanner planner;
    private final AccionBalanceoFactory fabrica;
    private final BalanceoInvoker invoker;
    private final EjecucionBalanceoRepository ejecucionRepository;
    private final AccionBalanceoRepository accionRepository;
    private final EventoDemandaRepository eventoRepository;
    private final AutorizacionService autorizacionService;

    public BalanceoService(
            BalanceService balanceService,
            BalanceoPlanner planner,
            AccionBalanceoFactory fabrica,
            BalanceoInvoker invoker,
            EjecucionBalanceoRepository ejecucionRepository,
            AccionBalanceoRepository accionRepository,
            EventoDemandaRepository eventoRepository,
            AutorizacionService autorizacionService) {

        this.balanceService = balanceService;
        this.planner = planner;
        this.fabrica = fabrica;
        this.invoker = invoker;
        this.ejecucionRepository = ejecucionRepository;
        this.accionRepository = accionRepository;
        this.eventoRepository = eventoRepository;
        this.autorizacionService = autorizacionService;
    }

    /** Simula el balanceo sin modificar nada. */
    @Transactional(readOnly = true)
    public PlanBalanceoResponse planificar(
            Long zonaId,
            String estrategia,
            Instant hasta,
            CondicionesProduccion condiciones) {

        Instant referencia = hasta != null ? hasta : Instant.now();

        AnalisisBalance analisis =
                balanceService.analizar(zonaId, referencia, condiciones);
        PlanCompleto plan = planner.planificar(analisis, estrategia);

        return PlanBalanceoResponse.desde(plan, analisis);
    }

    @Transactional
    public EjecucionResponse ejecutar(
            Usuario solicitante,
            Long zonaId,
            String estrategia,
            Instant hasta,
            CondicionesProduccion condiciones) {

        autorizacionService.validarAlgunRol(
                solicitante, Rol.ADMIN, Rol.OPERADOR);

        Instant referencia = hasta != null ? hasta : Instant.now();

        AnalisisBalance analisis =
                balanceService.analizar(zonaId, referencia, condiciones);
        PlanCompleto plan = planner.planificar(analisis, estrategia);

        if (plan.estaVacio()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No hay desbalances que corregir en la zona "
                            + "(o el déficit está dentro de la tolerancia)");
        }

        EjecucionBalanceo ejecucion = new EjecucionBalanceo();
        ejecucion.setZona(analisis.alcance());
        ejecucion.setEstrategia(plan.estrategia());
        ejecucion.setEstado(EstadoEjecucion.EJECUTADA);
        ejecucion.setInstanteReferencia(referencia);
        ejecucion.setIrradianciaWm2(
                BigDecimal.valueOf(condiciones.irradianciaWm2()));
        ejecucion.setVientoMs(BigDecimal.valueOf(condiciones.vientoMs()));
        ejecucion.setFactorDisponibilidad(
                BigDecimal.valueOf(condiciones.factorDisponibilidad()));
        ejecucion.setEjecutadaPor(solicitante.getUsername());
        ejecucionRepository.save(ejecucion);

        Map<Long, ZonaAnalizada> zonas = analisis.porId();
        List<AccionBalanceo> acciones =
                construirAcciones(ejecucion, plan, zonas);

        // Command: cada acción se ejecuta y queda auditada
        invoker.ejecutar(fabrica.comandos(acciones, zonas));

        return EjecucionResponse.desde(ejecucion, acciones);
    }

    @Transactional
    public EjecucionResponse revertir(Usuario solicitante, Long ejecucionId) {

        autorizacionService.validarAlgunRol(
                solicitante, Rol.ADMIN, Rol.OPERADOR);

        EjecucionBalanceo ejecucion = buscar(ejecucionId);

        if (ejecucion.getEstado() != EstadoEjecucion.EJECUTADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La ejecución " + ejecucionId + " ya fue revertida");
        }

        List<AccionBalanceo> acciones =
                accionRepository.findByEjecucionId(ejecucionId);

        CondicionesProduccion condiciones = new CondicionesProduccion(
                ejecucion.getIrradianciaWm2().doubleValue(),
                ejecucion.getVientoMs().doubleValue(),
                ejecucion.getFactorDisponibilidad().doubleValue());

        AnalisisBalance analisis = balanceService.analizar(
                ejecucion.getZona().getId(),
                ejecucion.getInstanteReferencia(),
                condiciones);

        // Command: deshacer en orden inverso
        invoker.deshacer(fabrica.comandos(acciones, analisis.porId()));

        ejecucion.setEstado(EstadoEjecucion.REVERTIDA);
        ejecucion.setRevertidaPor(solicitante.getUsername());
        ejecucion.setRevertidaEn(Instant.now());
        ejecucionRepository.save(ejecucion);

        return EjecucionResponse.desde(ejecucion, acciones);
    }

    @Transactional(readOnly = true)
    public EjecucionResponse obtener(Long ejecucionId) {
        EjecucionBalanceo ejecucion = buscar(ejecucionId);
        return EjecucionResponse.desde(ejecucion,
                accionRepository.findByEjecucionId(ejecucionId));
    }

    @Transactional(readOnly = true)
    public List<EjecucionResponse> listar(Long zonaId) {
        List<EjecucionBalanceo> ejecuciones = zonaId != null
                ? ejecucionRepository.findByZonaIdOrderByEjecutadaEnDesc(zonaId)
                : ejecucionRepository.findAllByOrderByEjecutadaEnDesc();

        return ejecuciones.stream()
                .map(e -> EjecucionResponse.desde(e,
                        accionRepository.findByEjecucionId(e.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoDemandaResponse> eventos(Long zonaId) {
        return eventoRepository.findByZonaIdOrderByFechaHoraDesc(zonaId)
                .stream()
                .map(EventoDemandaResponse::desde)
                .toList();
    }

    private List<AccionBalanceo> construirAcciones(
            EjecucionBalanceo ejecucion,
            PlanCompleto plan,
            Map<Long, ZonaAnalizada> zonas) {

        List<AccionBalanceo> acciones = new ArrayList<>();

        plan.transferencias().forEach(t -> {
            AccionBalanceo accion = new AccionBalanceo();
            accion.setEjecucion(ejecucion);
            accion.setTipo(TipoAccionBalanceo.TRANSFERENCIA);
            accion.setZonaOrigen(zonas.get(t.origenId()).zona());
            accion.setZona(zonas.get(t.destinoId()).zona());
            accion.setKw(kw(t.kw()));
            acciones.add(accion);
        });

        plan.deslastres().forEach(d -> {
            AccionBalanceo accion = new AccionBalanceo();
            accion.setEjecucion(ejecucion);
            accion.setTipo(TipoAccionBalanceo.DESLASTRE);
            accion.setZona(zonas.get(d.zonaId()).zona());
            accion.setKw(kw(d.kw()));
            acciones.add(accion);
        });

        return acciones;
    }

    private EjecucionBalanceo buscar(Long id) {
        return ejecucionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ejecución no encontrada: " + id));
    }

    private BigDecimal kw(double valor) {
        return BigDecimal.valueOf(valor).setScale(3, RoundingMode.HALF_UP);
    }
}