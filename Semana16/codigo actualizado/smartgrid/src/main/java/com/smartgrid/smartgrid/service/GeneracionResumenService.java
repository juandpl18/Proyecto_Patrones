package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.GeneracionResumenResponse;
import com.smartgrid.smartgrid.dto.GeneracionResumenResponse.FuenteResumen;
import com.smartgrid.smartgrid.dto.ProduccionResponse;
import com.smartgrid.smartgrid.repository.PlantaGeneracionRepository;
import com.smartgrid.smartgrid.strategy.CondicionesProduccion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GeneracionResumenService {

    private final PlantaGeneracionRepository plantaRepository;
    private final ProduccionService produccionService;

    public GeneracionResumenService(
            PlantaGeneracionRepository plantaRepository,
            ProduccionService produccionService) {

        this.plantaRepository = plantaRepository;
        this.produccionService = produccionService;
    }

    public GeneracionResumenResponse resumir(CondicionesProduccion condiciones) {

        List<ProduccionResponse> calculos = plantaRepository.findAll().stream()
                .map(p -> produccionService.calcular(p, condiciones))
                .toList();

        BigDecimal instalada = sumar(calculos,
                ProduccionResponse::capacidadKw);

        BigDecimal disponible = sumar(
                calculos.stream().filter(ProduccionResponse::generando).toList(),
                ProduccionResponse::capacidadKw);

        BigDecimal produccion = sumar(calculos,
                ProduccionResponse::produccionKw);

        Map<String, Long> porEstado = calculos.stream()
                .collect(Collectors.groupingBy(
                        ProduccionResponse::estado,
                        TreeMap::new,
                        Collectors.counting()));

        Map<String, List<ProduccionResponse>> porTipo = calculos.stream()
                .collect(Collectors.groupingBy(
                        ProduccionResponse::tipoFuente, TreeMap::new,
                        Collectors.toList()));

        List<FuenteResumen> porFuente = porTipo.entrySet().stream()
                .map(e -> new FuenteResumen(
                        e.getKey(),
                        e.getValue().size(),
                        sumar(e.getValue(), ProduccionResponse::capacidadKw),
                        sumar(e.getValue(), ProduccionResponse::produccionKw)))
                .toList();

        return new GeneracionResumenResponse(
                calculos.size(), instalada, disponible, produccion,
                porEstado, porFuente, condiciones);
    }

    private BigDecimal sumar(
            List<ProduccionResponse> lista,
            Function<ProduccionResponse, BigDecimal> campo) {

        return lista.stream()
                .map(campo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}