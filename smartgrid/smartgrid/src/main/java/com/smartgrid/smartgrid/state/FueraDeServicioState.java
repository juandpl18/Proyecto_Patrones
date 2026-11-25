package com.smartgrid.smartgrid.state;

import com.smartgrid.smartgrid.model.EstadoPlanta;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FueraDeServicioState implements EstadoPlantaState {

    @Override
    public EstadoPlanta estado() {
        return EstadoPlanta.FUERA_DE_SERVICIO;
    }

    @Override
    public boolean generaEnergia() {
        return false;
    }

    /** No vuelve directo a ACTIVA: primero debe pasar por mantenimiento. */
    @Override
    public Set<EstadoPlanta> transicionesPermitidas() {
        return Set.of(EstadoPlanta.MANTENIMIENTO);
    }
}