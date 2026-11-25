package com.smartgrid.smartgrid.chain.alivio;

/** Último recurso: reducir la carga de la zona en lo que falta. */
public class DeslastreRegla extends ReglaAlivio {

    @Override
    protected void manejar(AlivioContexto contexto) {
        contexto.deslastrar(contexto.getDeficitPendiente());
    }
}