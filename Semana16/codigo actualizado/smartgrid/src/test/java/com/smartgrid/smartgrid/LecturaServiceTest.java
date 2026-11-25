package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.dto.RegistrarLecturaRequest;
import com.smartgrid.smartgrid.model.LecturaConsumo;
import com.smartgrid.smartgrid.model.Medidor;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.observer.LecturaConsumoSubject;
import com.smartgrid.smartgrid.repository.LecturaConsumoRepository;
import com.smartgrid.smartgrid.repository.MedidorRepository;
import com.smartgrid.smartgrid.service.AutorizacionService;
import com.smartgrid.smartgrid.service.LecturaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LecturaServiceTest {

    private static final Instant FECHA = Instant.parse("2026-11-20T10:00:00Z");

    @Mock
    private LecturaConsumoRepository lecturaRepository;

    @Mock
    private MedidorRepository medidorRepository;

    @Mock
    private LecturaConsumoSubject subject;

    private LecturaService servicio;
    private Usuario operador;
    private Medidor medidor;

    @BeforeEach
    void preparar() {
        servicio = new LecturaService(lecturaRepository, medidorRepository,
                new AutorizacionService(), subject);

        operador = new Usuario("Op", "op", "x", Rol.OPERADOR, true);

        medidor = new Medidor();
        medidor.setCodigo("MED-1");
        medidor.setActivo(true);
    }

    private RegistrarLecturaRequest datos(String consumo) {
        return new RegistrarLecturaRequest(
                "MED-1", FECHA, new BigDecimal(consumo));
    }

    @Test
    void operadorRegistraLecturaYSeNotificaAlObservador() {
        when(medidorRepository.findByCodigo("MED-1"))
                .thenReturn(Optional.of(medidor));
        when(lecturaRepository.existsByMedidorIdAndFechaHora(any(), any()))
                .thenReturn(false);
        when(lecturaRepository.save(any(LecturaConsumo.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var respuesta = servicio.registrar(operador, datos("42.5"));

        assertEquals("MED-1", respuesta.codigoMedidor());
        verify(subject).notificar(any(LecturaConsumo.class));
    }

    @Test
    void rechazaConsumoNegativo() {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> servicio.registrar(operador, datos("-5")));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(subject, never()).notificar(any());
    }

    @Test
    void medidorInexistenteDevuelve404() {
        when(medidorRepository.findByCodigo("MED-1"))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> servicio.registrar(operador, datos("10")));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void lecturaDuplicadaDevuelve409() {
        when(medidorRepository.findByCodigo("MED-1"))
                .thenReturn(Optional.of(medidor));
        when(lecturaRepository.existsByMedidorIdAndFechaHora(any(), any()))
                .thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> servicio.registrar(operador, datos("10")));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(lecturaRepository, never()).save(any());
    }
}