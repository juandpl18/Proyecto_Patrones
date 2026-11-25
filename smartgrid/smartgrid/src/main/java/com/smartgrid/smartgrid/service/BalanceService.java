package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.balanceo.AnalisisBalance;
import com.smartgrid.smartgrid.balanceo.Potencia;
import com.smartgrid.smartgrid.balanceo.ZonaAnalizada;
import com.smartgrid.smartgrid.chain.alivio.PlantaRecuperable;
import com.smartgrid.smartgrid.dto.BalanceResponse;
import com.smartgrid.smartgrid.dto.ProduccionResponse;
import com.smartgrid.smartgrid.dto.ZonaBalanceResponse;
import com.smartgrid.smartgrid.model.AccionBalanceo;
import com.smartgrid.smartgrid.model.EstadoEjecucion;
import com.smartgrid.smartgrid.model.EstadoPlanta;
import com.smartgrid.smartgrid.model.PlantaGeneracion;
import com.smartgrid.smartgrid.model.TipoAccionBalanceo;
import com.smartgrid.smartgrid.model.ZonaRed;
import com.smartgrid.smartgrid.repository.AccionBalanceoRepository;
import com.smartgrid.smartgrid.repository.ConsumoPorZona;
import com.smartgrid.smartgrid.repository.LecturaConsumoRepository;
import com.smartgrid.smartgrid.repository.PlantaGeneracionRepository;
import com.smartgrid.smartgrid.repository.ZonaRedRepository;
import com.smartgrid.smartgrid.strategy.CondicionesProduccion;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BalanceService {

    /** La demanda de una zona es el consumo de la última hora. */
    private static final long VENTANA_HORAS = 1;

    /** Una ejecución de balanceo afecta el balance durante una hora. */
    private static final long VIGENCIA_ACCIONES_MINUTOS = 60;

    private final ZonaRedRepository zonaRepository;
    private final LecturaConsumoRepository lecturaRepository;
    private final PlantaGeneracionRepository plantaRepository;
    private final AccionBalanceoRepository accionRepository;
    private final ProduccionService produccionService;

    public BalanceService(
            ZonaRedRepository zonaRepository,
            LecturaConsumoRepository lecturaRepository,
            PlantaGeneracionRepository plantaRepository,
            AccionBalanceoRepository accionRepository,
            ProduccionService produccionService) {

        this.zonaRepository = zonaRepository;
        this.lecturaRepository = lecturaRepository;
        this.plantaRepository = plantaRepository;
        this.accionRepository = accionRepository;
        this.produccionService = produccionService;
    }

    public BalanceResponse diagnosticar(
            Long zonaId, Instant hasta, CondicionesProduccion condiciones) {

        Instant referencia = hasta != null ? hasta : Instant.now();
        AnalisisBalance analisis = analizar(zonaId, referencia, condiciones);

        List<ZonaBalanceResponse> zonas = analisis.zonas().stream()
                .map(z -> new ZonaBalanceResponse(
                        z.zona().getId(),
                        z.zona().getNombre(),
                        z.zona().getTipo().name(),
                        Potencia.kw(z.demandaKw()),
                        Potencia.kw(z.generacionKw()),
                        Potencia.kw(z.transferenciasNetasKw()),
                        Potencia.kw(z.deslastreKw()),
                        Potencia.kw(z.balanceKw()),
                        Potencia.estadoDe(z.balanceKw()).name()))
                .toList();

        double demanda = analisis.zonas().stream()
                .mapToDouble(ZonaAnalizada::demandaKw).sum();
        double generacion = analisis.zonas().stream()
                .mapToDouble(ZonaAnalizada::generacionKw).sum();
        double balance = analisis.zonas().stream()
                .mapToDouble(ZonaAnalizada::balanceKw).sum();

        return new BalanceResponse(
                analisis.alcance().getId(),
                analisis.alcance().getNombre(),
                analisis.desde(),
                analisis.hasta(),
                Potencia.kw(demanda),
                Potencia.kw(generacion),
                Potencia.kw(balance),
                Potencia.estadoDe(balance).name(),
                zonas);
    }

    /**
     * balance = generación + transferencias netas - (demanda - deslastre),
     * para cada zona hija de la zona indicada.
     */
    public AnalisisBalance analizar(
            Long zonaId, Instant hasta, CondicionesProduccion condiciones) {

        ZonaRed alcance = zonaRepository.findById(zonaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Zona no encontrada: " + zonaId));

        List<ZonaRed> todas = zonaRepository.findAll();

        Map<Long, List<ZonaRed>> hijosPorPadre = todas.stream()
                .filter(z -> z.getZonaPadre() != null)
                .collect(Collectors.groupingBy(
                        z -> z.getZonaPadre().getId()));

        List<ZonaRed> hijas = hijosPorPadre.getOrDefault(zonaId, List.of())
                .stream()
                .sorted(Comparator.comparing(ZonaRed::getId))
                .toList();

        if (hijas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La zona '" + alcance.getNombre()
                            + "' no tiene zonas hijas para balancear");
        }

        Instant desde = hasta.minus(VENTANA_HORAS, ChronoUnit.HOURS);

        // Demanda directa por zona (una sola consulta agrupada)
        Map<Long, Double> consumoDirecto = new HashMap<>();
        for (ConsumoPorZona c : lecturaRepository.consumoPorZona(desde, hasta)) {
            consumoDirecto.put(c.getZonaId(), c.getTotal().doubleValue());
        }

        // Generación directa por zona (Strategy + State de la Fase 3)
        Map<Long, Double> generacionDirecta = new HashMap<>();
        Map<Long, List<PlantaRecuperable>> recuperablesDirectos =
                new HashMap<>();

        for (PlantaGeneracion planta : plantaRepository.findAll()) {
            if (planta.getZona() == null) {
                continue;
            }
            Long plantaZonaId = planta.getZona().getId();
            ProduccionResponse produccion =
                    produccionService.calcular(planta, condiciones);

            generacionDirecta.merge(plantaZonaId,
                    produccion.produccionKw().doubleValue(), Double::sum);

            if (planta.getEstado() == EstadoPlanta.MANTENIMIENTO) {
                recuperablesDirectos
                        .computeIfAbsent(plantaZonaId, k -> new ArrayList<>())
                        .add(new PlantaRecuperable(planta.getNombre(),
                                produccion.potencialKw().doubleValue()));
            }
        }

        // Ajustes de balanceos ejecutados y todavía vigentes
        Map<Long, Double> transferenciasNetas = new HashMap<>();
        Map<Long, Double> deslastres = new HashMap<>();

        for (AccionBalanceo accion : accionRepository.vigentes(
                EstadoEjecucion.EJECUTADA,
                hasta.minus(VIGENCIA_ACCIONES_MINUTOS, ChronoUnit.MINUTES),
                hasta)) {

            double kw = accion.getKw().doubleValue();
            Long destino = accion.getZona().getId();

            if (accion.getTipo() == TipoAccionBalanceo.TRANSFERENCIA) {
                transferenciasNetas.merge(destino, kw, Double::sum);
                transferenciasNetas.merge(
                        accion.getZonaOrigen().getId(), -kw, Double::sum);
            } else {
                deslastres.merge(destino, kw, Double::sum);
            }
        }

        List<ZonaAnalizada> zonas = new ArrayList<>();

        for (ZonaRed hija : hijas) {
            Set<Long> subarbol = subarbol(hija.getId(), hijosPorPadre);

            double demanda = sumar(subarbol, consumoDirecto);
            double generacion = sumar(subarbol, generacionDirecta);
            double neta = transferenciasNetas.getOrDefault(hija.getId(), 0.0);
            double deslastre = deslastres.getOrDefault(hija.getId(), 0.0);

            List<PlantaRecuperable> recuperables = subarbol.stream()
                    .flatMap(id -> recuperablesDirectos
                            .getOrDefault(id, List.of()).stream())
                    .toList();

            double balance = generacion + neta - (demanda - deslastre);

            zonas.add(new ZonaAnalizada(hija, demanda, generacion,
                    neta, deslastre, balance, recuperables));
        }

        return new AnalisisBalance(alcance, desde, hasta, zonas);
    }

    /** Ids de la zona y de todas sus descendientes. */
    private Set<Long> subarbol(
            Long raizId, Map<Long, List<ZonaRed>> hijosPorPadre) {

        Set<Long> ids = new HashSet<>();
        Deque<Long> pendientes = new ArrayDeque<>();
        pendientes.push(raizId);

        while (!pendientes.isEmpty()) {
            Long id = pendientes.pop();
            if (ids.add(id)) {
                hijosPorPadre.getOrDefault(id, List.of())
                        .forEach(hijo -> pendientes.push(hijo.getId()));
            }
        }
        return ids;
    }

    private double sumar(Set<Long> ids, Map<Long, Double> valores) {
        return ids.stream()
                .mapToDouble(id -> valores.getOrDefault(id, 0.0))
                .sum();
    }
}