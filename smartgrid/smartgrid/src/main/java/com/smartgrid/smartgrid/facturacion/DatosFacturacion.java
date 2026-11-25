package com.smartgrid.smartgrid.facturacion;

import com.smartgrid.smartgrid.model.TipoCliente;

import java.math.BigDecimal;
import java.util.List;

/** Lo que se factura: el consumo total y, si existen, las lecturas por hora. */
public record DatosFacturacion(
        TipoCliente tipoCliente,
        BigDecimal consumoKwh,
        List<LecturaHora> lecturas) {
}