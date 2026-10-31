package com.smartgrid.smartgrid.chain;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Component;

@Component
public class CapacidadValidacion extends ValidacionHandler {

    @Override
    protected void procesarValidacion(
            String nombre,
            String fuente,
            Double capacidad) {

        if (capacidad != null && capacidad <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La capacidad debe ser mayor que cero"
            );
        }
    }
}