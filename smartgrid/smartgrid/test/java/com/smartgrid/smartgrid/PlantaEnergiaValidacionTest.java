package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.chain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlantaEnergiaValidacionTest {

    private final NombreValidacion nombre = new NombreValidacion();
    private final FuenteEnergiaValidacion fuente =
            new FuenteEnergiaValidacion();
    private final CapacidadValidacion capacidad =
            new CapacidadValidacion();

    private final PlantaEnergiaValidacionChain cadena =
            new PlantaEnergiaValidacionChain(
                    nombre, fuente, capacidad
            );

    @Test
    void validarPlantaCorrecta() {

        assertDoesNotThrow(() ->
                cadena.validar(
                        "Planta Solar",
                        "SOLAR",
                        500.0
                )
        );
    }

    @Test
    void rechazarNombreVacio() {

        assertThrows(IllegalArgumentException.class, () ->
                cadena.validar(
                        "",
                        "SOLAR",
                        500.0
                )
        );
    }

    @Test
    void rechazarFuenteInvalida() {

        assertThrows(IllegalArgumentException.class, () ->
                cadena.validar(
                        "Planta Solar",
                        "NUCLEAR",
                        500.0
                )
        );
    }

    @Test
    void rechazarCapacidadNegativa() {

        assertThrows(IllegalArgumentException.class, () ->
                cadena.validar(
                        "Planta Solar",
                        "SOLAR",
                        -100.0
                )
        );
    }
}