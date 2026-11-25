package com.smartgrid.smartgrid.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

final class EnumParser {

    private EnumParser() {
    }

    static <E extends Enum<E>> E parsear(
            Class<E> tipo, String texto, String campo) {

        try {
            return Enum.valueOf(tipo, texto.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Valor inválido para " + campo + ". Opciones: "
                            + Arrays.toString(tipo.getEnumConstants()));
        }
    }
}