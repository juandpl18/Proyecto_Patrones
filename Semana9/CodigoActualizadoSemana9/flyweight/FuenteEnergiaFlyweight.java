
package com.smartgrid.smartgrid.flyweight;

public class FuenteEnergiaFlyweight {

    private final String tipo;
    private final String descripcion;
    private final String caracteristicas;

    public FuenteEnergiaFlyweight(
            String tipo,
            String descripcion,
            String caracteristicas) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.caracteristicas = caracteristicas;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }
}
