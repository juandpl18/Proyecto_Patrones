package com.smartgrid.smartgrid.chain;

import org.springframework.stereotype.Component;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Component
public class NombreValidacion extends ValidacionHandler {

    @Override
    protected void procesarValidacion(
            String nombre,
            String fuente,
            Double capacidad) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre de la planta es obligatorio"
            );
        }
    }
}

