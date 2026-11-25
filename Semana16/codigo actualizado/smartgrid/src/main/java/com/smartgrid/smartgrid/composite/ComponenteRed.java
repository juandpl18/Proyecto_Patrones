package com.smartgrid.smartgrid.composite;

import java.math.BigDecimal;
import java.util.List;

/** Componente común: tanto una hoja como un grupo se consultan igual. */
public interface ComponenteRed {

    Long getId();

    String getNombre();

    String getTipo();

    BigDecimal getCapacidadKw();

    BigDecimal getConsumoKwh();

    List<ComponenteRed> getHijos();
}