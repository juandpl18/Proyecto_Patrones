package com.smartgrid.smartgrid.state;

import com.smartgrid.smartgrid.model.EstadoPlanta;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ActivaState implements EstadoPlantaState {

    @Override
    public EstadoPlanta estado() {
        return EstadoPlanta.ACTIVA;
    }

    @Override
    public boolean generaEnergia() {
        return true;
    }

    @Override
    public Set<EstadoPlanta> transicionesPermitidas() {
        return Set.of(EstadoPlanta.MANTENIMIENTO,
                EstadoPlanta.FUERA_DE_SERVICIO);
    }
}