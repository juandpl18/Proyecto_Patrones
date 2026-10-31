
package com.smartgrid.smartgrid.dto;

public class PanelResumenResponse {

    private String estadoSistema;
    private FacturaResultado factura;

    public PanelResumenResponse(
            String estadoSistema,
            FacturaResultado factura) {
        this.estadoSistema = estadoSistema;
        this.factura = factura;
    }

    public String getEstadoSistema() {
        return estadoSistema;
    }

    public FacturaResultado getFactura() {
        return factura;
    }
}