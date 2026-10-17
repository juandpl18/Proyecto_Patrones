
package com.smartgrid.smartgrid.dto;

public class FacturaResultado {

    private String tipoFactura;
    private double consumo;
    private double tarifa;
    private double total;

    public FacturaResultado(
            String tipoFactura,
            double consumo,
            double tarifa,
            double total) {
        this.tipoFactura = tipoFactura;
        this.consumo = consumo;
        this.tarifa = tarifa;
        this.total = total;
    }

    public String getTipoFactura() {
        return tipoFactura;
    }

    public double getConsumo() {
        return consumo;
    }

    public double getTarifa() {
        return tarifa;
    }

    public double getTotal() {
        return total;
    }
}