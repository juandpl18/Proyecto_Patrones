package com.smartgrid.smartgrid.chain.alivio;

/** Un déficit de hasta el 2 % de la demanda se considera tolerable. */
public class ToleranciaRegla extends ReglaAlivio {

    private static final double PORCENTAJE_TOLERABLE = 0.02;

    @Override
    protected void manejar(AlivioContexto contexto) {
        double limite = contexto.getDemandaKw() * PORCENTAJE_TOLERABLE;

        if (contexto.getDeficitPendiente() <= limite) {
            contexto.aceptarDeficit(
                    "el déficit está dentro de la tolerancia (2% de la demanda)");
        }
    }
}