package com.smartgrid.smartgrid.observer;

import com.smartgrid.smartgrid.model.LecturaConsumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sujeto observado. Spring inyecta automáticamente TODOS los beans que
 * implementen LecturaObserver, así que agregar un observador nuevo no
 * requiere modificar esta clase (principio Abierto/Cerrado).
 */
@Component
public class LecturaConsumoSubject {

    private static final Logger log =
            LoggerFactory.getLogger(LecturaConsumoSubject.class);

    private final List<LecturaObserver> observadores;

    public LecturaConsumoSubject(List<LecturaObserver> observadores) {
        this.observadores = observadores;
    }

    public void notificar(LecturaConsumo lectura) {
        for (LecturaObserver observador : observadores) {
            try {
                observador.alRegistrarLectura(lectura);
            } catch (RuntimeException e) {
                // Un observador defectuoso no debe impedir registrar la lectura
                log.warn("Observador {} falló: {}",
                        observador.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}