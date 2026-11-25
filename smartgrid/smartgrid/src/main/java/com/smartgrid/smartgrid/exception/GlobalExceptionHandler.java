package com.smartgrid.smartgrid.exception;

import com.smartgrid.smartgrid.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> estado(
            ResponseStatusException ex, HttpServletRequest req) {

        String mensaje = ex.getReason() != null
                ? ex.getReason() : "Error en la solicitud";
        return construir(ex.getStatusCode().value(), mensaje, req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> argumentoInvalido(
            IllegalArgumentException ex, HttpServletRequest req) {

        return construir(400, ex.getMessage(), req);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> parametroFaltante(
            MissingServletRequestParameterException ex,
            HttpServletRequest req) {

        return construir(400,
                "Falta el parámetro: " + ex.getParameterName(), req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> tipoIncorrecto(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest req) {

        return construir(400,
                "Valor inválido para el parámetro: " + ex.getName(), req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(
            DataIntegrityViolationException ex, HttpServletRequest req) {

        return construir(409,
                "La operación viola una restricción de datos "
                        + "(registro duplicado o referencia inválida)", req);
    }

    private ResponseEntity<ErrorResponse> construir(
            int status, String mensaje, HttpServletRequest req) {

        HttpStatus http = HttpStatus.resolve(status);
        String error = http != null ? http.getReasonPhrase() : "Error";

        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        Instant.now(), status, error,
                        mensaje, req.getRequestURI()));
    }
}