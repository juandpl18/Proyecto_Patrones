package com.smartgrid.smartgrid.observer;

import com.smartgrid.smartgrid.model.Alerta;
import com.smartgrid.smartgrid.model.LecturaConsumo;
import com.smartgrid.smartgrid.model.Severidad;
import com.smartgrid.smartgrid.model.TipoAlerta;
import com.smartgrid.smartgrid.model.ZonaRed;
import com.smartgrid.smartgrid.repository.AlertaRepository;
import com.smartgrid.smartgrid.repository.LecturaConsumoRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Genera una alerta cuando el consumo de la última hora en la zona del
 * medidor alcanza el 90 % (ADVERTENCIA) o supera el 100 % (CRITICA)
 * de su capacidad.
 */
@Component
public class AlertaCapacidadObserver implements LecturaObserver {

    private static final BigDecimal UMBRAL_ADVERTENCIA = new BigDecimal("0.90");

    private final LecturaConsumoRepository lecturaRepository;
    private final AlertaRepository alertaRepository;

    public AlertaCapacidadObserver(
            LecturaConsumoRepository lecturaRepository,
            AlertaRepository alertaRepository) {

        this.lecturaRepository = lecturaRepository;
        this.alertaRepository = alertaRepository;
    }

    @Override
    public void alRegistrarLectura(LecturaConsumo lectura) {

        ZonaRed zona = lectura.getMedidor().getZona();
        BigDecimal capacidad = zona.getCapacidadKw();

        if (capacidad == null || capacidad.signum() <= 0) {
            return; // la zona no tiene capacidad definida
        }

        Instant hasta = lectura.getFechaHora();
        Instant desde = hasta.minus(1, ChronoUnit.HOURS);

        BigDecimal demanda =
                lecturaRepository.sumaConsumoPorZona(zona.getId(), desde, hasta);
        if (demanda == null) {
            demanda = BigDecimal.ZERO;
        }

        if (demanda.compareTo(capacidad) > 0) {
            crearAlerta(zona, Severidad.CRITICA,
                    "Demanda de " + demanda + " kW supera la capacidad de "
                            + capacidad + " kW en " + zona.getNombre());

        } else if (demanda.compareTo(
                capacidad.multiply(UMBRAL_ADVERTENCIA)) >= 0) {
            crearAlerta(zona, Severidad.ADVERTENCIA,
                    "Demanda de " + demanda + " kW alcanzó el 90% de la "
                            + "capacidad de " + capacidad + " kW en "
                            + zona.getNombre());
        }
    }

    private void crearAlerta(ZonaRed zona, Severidad severidad, String mensaje) {

        if (alertaRepository.existsByZonaIdAndTipoAndSeveridadAndAtendidaFalse(
                zona.getId(), TipoAlerta.PICO_DEMANDA, severidad)) {
            return; // ya hay una alerta pendiente igual
        }

        Alerta alerta = new Alerta();
        alerta.setZona(zona);
        alerta.setTipo(TipoAlerta.PICO_DEMANDA);
        alerta.setSeveridad(severidad);
        alerta.setMensaje(mensaje);

        alertaRepository.save(alerta);
    }
}