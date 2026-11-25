package com.smartgrid.smartgrid.dto;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Rango de fechas de una consulta (ambos extremos incluidos). */
public record Periodo(Instant desde, Instant hasta) {

    /** Si no se indica: los últimos 30 días hasta ahora. */
    public static Periodo de(Instant desde, Instant hasta) {

        Instant fin = hasta != null ? hasta : Instant.now();
        Instant inicio = desde != null
                ? desde : fin.minus(30, ChronoUnit.DAYS);

        if (inicio.isAfter(fin)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'desde' no puede ser posterior a 'hasta'");
        }

        return new Periodo(inicio, fin);
    }
}