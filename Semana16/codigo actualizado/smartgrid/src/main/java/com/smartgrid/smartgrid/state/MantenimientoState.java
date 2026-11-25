package com.smartgrid.smartgrid.state;

import com.smartgrid.smartgrid.model.EstadoPlanta;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MantenimientoState implements EstadoPlantaState {

    @Override
    public EstadoPlanta estado() {
        return EstadoPlanta.MANTENIMIENTO;
    }

    @Override
    public boolean generaEnergia() {
        return false;
    }

    @Override
    public Set<EstadoPlanta> transicionesPermitidas() {
        return Set.of(EstadoPlanta.ACTIVA,
                EstadoPlanta.FUERA_DE_SERVICIO);
    }
}