package com.smartgrid.smartgrid.chain.alivio;

import com.smartgrid.smartgrid.balanceo.Potencia;

/** Eslabón de la cadena: aplica su regla y pasa al siguiente si queda déficit. */
public abstract class ReglaAlivio {

    private ReglaAlivio siguiente;

    /** Devuelve el eslabón recibido para poder encadenar varias llamadas. */
    public ReglaAlivio setSiguiente(ReglaAlivio siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    public final void aplicar(AlivioContexto contexto) {
        manejar(contexto);

        if (siguiente != null
                && contexto.getDeficitPendiente() > Potencia.TOLERANCIA_KW) {
            siguiente.aplicar(contexto);
        }
    }

    protected abstract void manejar(AlivioContexto contexto);
}