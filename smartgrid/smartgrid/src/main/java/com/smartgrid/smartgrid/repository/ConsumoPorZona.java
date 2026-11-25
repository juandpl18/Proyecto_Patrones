package com.smartgrid.smartgrid.repository;

import java.math.BigDecimal;

/** Proyección: consumo total agrupado por zona. */
public interface ConsumoPorZona {

    Long getZonaId();

    BigDecimal getTotal();
}