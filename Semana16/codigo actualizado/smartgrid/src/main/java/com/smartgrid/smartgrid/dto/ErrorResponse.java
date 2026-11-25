package com.smartgrid.smartgrid.dto;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String mensaje,
        String ruta) {
}