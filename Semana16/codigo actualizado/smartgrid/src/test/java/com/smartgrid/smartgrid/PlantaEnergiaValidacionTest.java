package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.chain.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class PlantaEnergiaValidacionTest {

    private final PlantaEnergiaValidacionChain cadena =
            new PlantaEnergiaValidacionChain(
                    new NombreValidacion(),
                    new FuenteEnergiaValidacion(),
                    new CapacidadValidacion()
            );

    private void assertBadRequest(String nombre, String fuente, Double cap) {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> cadena.validar(nombre, fuente, cap));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void validarPlantaCorrecta() {
        assertDoesNotThrow(() ->
                cadena.validar("Planta Solar", "SOLAR", 500.0));
    }

    @Test
    void rechazarNombreVacio() {
        assertBadRequest("", "SOLAR", 500.0);
    }

    @Test
    void rechazarFuenteInvalida() {
        assertBadRequest("Planta Solar", "NUCLEAR", 500.0);
    }

    @Test
    void rechazarCapacidadNegativa() {
        assertBadRequest("Planta Solar", "SOLAR", -100.0);
    }
}