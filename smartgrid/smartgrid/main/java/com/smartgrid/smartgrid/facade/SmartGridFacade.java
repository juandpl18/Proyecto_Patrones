
package com.smartgrid.smartgrid.facade;

import com.smartgrid.smartgrid.service.FacturacionService;
import com.smartgrid.smartgrid.service.SmartGridService;
import org.springframework.stereotype.Service;
import com.smartgrid.smartgrid.dto.FacturaResultado;
import com.smartgrid.smartgrid.dto.PanelResumenResponse;

@Service
public class SmartGridFacade {

    private final SmartGridService smartGridService;
    private final FacturacionService facturacionService;

    public SmartGridFacade(
            SmartGridService smartGridService,
            FacturacionService facturacionService) {

        this.smartGridService = smartGridService;
        this.facturacionService = facturacionService;
    }

    public String obtenerResumen() {
        return smartGridService.obtenerEstado();
    }

    public String generarFactura(String tipo, double consumo) {
        return facturacionService.generarFactura(tipo, consumo);
    }

    public String obtenerPanel(String tipo, double consumo) {
        String estado = smartGridService.obtenerEstado();
        String factura = facturacionService.generarFactura(tipo, consumo);

        return "=== PANEL SMARTGRID ===\n"
                + "Estado del sistema: " + estado + "\n"
                + "Resultado de facturación: " + factura;
    }


    public PanelResumenResponse obtenerPanelVisual(
            String tipo, double consumo) {

        String estado = smartGridService.obtenerEstado();

        FacturaResultado factura =
                facturacionService.obtenerResultado(tipo, consumo);

        return new PanelResumenResponse(estado, factura);
    }
}