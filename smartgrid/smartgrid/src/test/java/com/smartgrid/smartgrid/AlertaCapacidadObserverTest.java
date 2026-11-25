package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.model.*;
import com.smartgrid.smartgrid.observer.AlertaCapacidadObserver;
import com.smartgrid.smartgrid.repository.AlertaRepository;
import com.smartgrid.smartgrid.repository.LecturaConsumoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaCapacidadObserverTest {

    @Mock
    private LecturaConsumoRepository lecturas;

    @Mock
    private AlertaRepository alertas;

    @InjectMocks
    private AlertaCapacidadObserver observer;

    private LecturaConsumo lectura(BigDecimal capacidad) {
        ZonaRed zona = new ZonaRed();
        ReflectionTestUtils.setField(zona, "id", 7L);
        zona.setNombre("Sector A");
        zona.setCapacidadKw(capacidad);

        Medidor medidor = new Medidor();
        medidor.setZona(zona);

        LecturaConsumo lectura = new LecturaConsumo();
        lectura.setMedidor(medidor);
        lectura.setFechaHora(Instant.parse("2026-11-20T10:00:00Z"));
        lectura.setConsumoKwh(BigDecimal.TEN);
        return lectura;
    }

    private void demandaDe(String kw) {
        when(lecturas.sumaConsumoPorZona(eq(7L), any(), any()))
                .thenReturn(new BigDecimal(kw));
    }

    @Test
    void demandaAl95PorCientoCreaAdvertencia() {
        demandaDe("95");
        when(alertas.existsByZonaIdAndTipoAndSeveridadAndAtendidaFalse(
                7L, TipoAlerta.PICO_DEMANDA, Severidad.ADVERTENCIA))
                .thenReturn(false);

        observer.alRegistrarLectura(lectura(new BigDecimal("100")));

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertas).save(captor.capture());
        assertEquals(Severidad.ADVERTENCIA, captor.getValue().getSeveridad());
    }

    @Test
    void demandaSobreLaCapacidadCreaAlertaCritica() {
        demandaDe("120");
        when(alertas.existsByZonaIdAndTipoAndSeveridadAndAtendidaFalse(
                7L, TipoAlerta.PICO_DEMANDA, Severidad.CRITICA))
                .thenReturn(false);

        observer.alRegistrarLectura(lectura(new BigDecimal("100")));

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertas).save(captor.capture());
        assertEquals(Severidad.CRITICA, captor.getValue().getSeveridad());
    }

    @Test
    void demandaNormalNoCreaAlerta() {
        demandaDe("50");

        observer.alRegistrarLectura(lectura(new BigDecimal("100")));

        verify(alertas, never()).save(any());
    }

    @Test
    void noDuplicaUnaAlertaPendiente() {
        demandaDe("120");
        when(alertas.existsByZonaIdAndTipoAndSeveridadAndAtendidaFalse(
                7L, TipoAlerta.PICO_DEMANDA, Severidad.CRITICA))
                .thenReturn(true);

        observer.alRegistrarLectura(lectura(new BigDecimal("100")));

        verify(alertas, never()).save(any());
    }

    @Test
    void zonaSinCapacidadNoSeEvalua() {
        observer.alRegistrarLectura(lectura(null));

        verifyNoInteractions(lecturas);
        verify(alertas, never()).save(any());
    }
}