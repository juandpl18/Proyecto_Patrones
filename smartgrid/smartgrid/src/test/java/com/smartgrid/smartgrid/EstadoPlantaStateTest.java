package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.model.EstadoPlanta;
import com.smartgrid.smartgrid.state.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EstadoPlantaStateTest {

    private final EstadoPlantaResolver resolver = new EstadoPlantaResolver(
            List.of(new ActivaState(), new MantenimientoState(),
                    new FueraDeServicioState()));

    @Test
    void soloLaPlantaActivaGeneraEnergia() {
        assertTrue(resolver.de(EstadoPlanta.ACTIVA).generaEnergia());
        assertFalse(resolver.de(EstadoPlanta.MANTENIMIENTO).generaEnergia());
        assertFalse(resolver.de(EstadoPlanta.FUERA_DE_SERVICIO).generaEnergia());
    }

    @Test
    void transicionesPermitidas() {
        assertTrue(resolver.de(EstadoPlanta.ACTIVA)
                .puedePasarA(EstadoPlanta.MANTENIMIENTO));
        assertTrue(resolver.de(EstadoPlanta.MANTENIMIENTO)
                .puedePasarA(EstadoPlanta.ACTIVA));
        assertTrue(resolver.de(EstadoPlanta.FUERA_DE_SERVICIO)
                .puedePasarA(EstadoPlanta.MANTENIMIENTO));
    }

    @Test
    void fueraDeServicioNoVuelveDirectoAActiva() {
        assertFalse(resolver.de(EstadoPlanta.FUERA_DE_SERVICIO)
                .puedePasarA(EstadoPlanta.ACTIVA));
    }
}