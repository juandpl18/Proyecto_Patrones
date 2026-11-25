package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.composite.ZonaCompuesta;
import com.smartgrid.smartgrid.composite.ZonaHoja;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ComponenteRedCompositeTest {

    @Test
    void consumoSeSumaHaciaArriba() {

        ZonaHoja sectorA = new ZonaHoja(3L, "Sector A", "SECTOR",
                new BigDecimal("100"), new BigDecimal("100"));
        ZonaHoja sectorB = new ZonaHoja(4L, "Sector B", "SECTOR",
                new BigDecimal("80"), new BigDecimal("50"));

        ZonaCompuesta zona = new ZonaCompuesta(2L, "Zona 1", "ZONA",
                null, new BigDecimal("10"));
        zona.agregar(sectorA);
        zona.agregar(sectorB);

        ZonaCompuesta subestacion = new ZonaCompuesta(1L, "Subestación",
                "SUBESTACION", null, BigDecimal.ZERO);
        subestacion.agregar(zona);

        assertEquals(0, new BigDecimal("100").compareTo(sectorA.getConsumoKwh()));
        assertEquals(0, new BigDecimal("160").compareTo(zona.getConsumoKwh()));
        assertEquals(0, new BigDecimal("160").compareTo(subestacion.getConsumoKwh()));
        assertTrue(sectorA.getHijos().isEmpty());
        assertEquals(1, subestacion.getHijos().size());
    }
}