package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.ProduccionResponse;
import com.smartgrid.smartgrid.model.PlantaGeneracion;
import com.smartgrid.smartgrid.repository.PlantaGeneracionRepository;
import com.smartgrid.smartgrid.state.EstadoPlantaResolver;
import com.smartgrid.smartgrid.strategy.CondicionesProduccion;
import com.smartgrid.smartgrid.strategy.ProduccionStrategy;
import com.smartgrid.smartgrid.strategy.ProduccionStrategyRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ProduccionService {

    private final PlantaGeneracionRepository plantaRepository;
    private final ProduccionStrategyRegistry estrategias;
    private final EstadoPlantaResolver estados;

    public ProduccionService(
            PlantaGeneracionRepository plantaRepository,
            ProduccionStrategyRegistry estrategias,
            EstadoPlantaResolver estados) {

        this.plantaRepository = plantaRepository;
        this.estrategias = estrategias;
        this.estados = estados;
    }

    @Transactional(readOnly = true)
    public ProduccionResponse estimar(
            Long plantaId, CondicionesProduccion condiciones) {

        PlantaGeneracion planta = plantaRepository.findById(plantaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Planta no encontrada: " + plantaId));

        return calcular(planta, condiciones);
    }

    /** Strategy decide CÓMO se calcula; State decide SI la planta genera. */
    public ProduccionResponse calcular(
            PlantaGeneracion planta, CondicionesProduccion condiciones) {

        ProduccionStrategy estrategia = estrategias.para(planta.getTipoFuente());
        boolean generando = estados.de(planta.getEstado()).generaEnergia();

        double capacidad = planta.getCapacidadKw().doubleValue();
        double potencial = estrategia.calcularKw(capacidad, condiciones);
        double real = generando ? potencial : 0.0;

        BigDecimal produccionKw = kw(real);
        BigDecimal factor = planta.getCapacidadKw().signum() > 0
                ? produccionKw.divide(
                planta.getCapacidadKw(), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new ProduccionResponse(
                planta.getId(),
                planta.getNombre(),
                planta.getTipoFuente().name(),
                planta.getEstado().name(),
                generando,
                planta.getCapacidadKw(),
                kw(potencial),
                produccionKw,
                factor,
                estrategia.metodo());
    }

    private BigDecimal kw(double valor) {
        return BigDecimal.valueOf(valor).setScale(3, RoundingMode.HALF_UP);
    }
}