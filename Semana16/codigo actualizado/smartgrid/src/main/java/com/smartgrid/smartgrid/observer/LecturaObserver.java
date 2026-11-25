package com.smartgrid.smartgrid.observer;

import com.smartgrid.smartgrid.model.LecturaConsumo;

/** Observador: reacciona cuando se registra una lectura. */
public interface LecturaObserver {

    void alRegistrarLectura(LecturaConsumo lectura);
}