package com.smartgrid.smartgrid.command.balanceo;

import com.smartgrid.smartgrid.balanceo.Potencia;
import com.smartgrid.smartgrid.model.AccionBalanceo;
import com.smartgrid.smartgrid.model.EventoDemanda;
import com.smartgrid.smartgrid.repository.AccionBalanceoRepository;
import com.smartgrid.smartgrid.repository.EventoDemandaRepository;

import java.time.Instant;
import java.util.List;

/**
 * Ejecutar: guarda la acción y audita el hecho en evento_demanda.
 * Deshacer: audita la reversión (el efecto desaparece porque la
 * ejecución pasa a REVERTIDA y deja de contar en el balance).
 */
public abstract class AccionBalanceoBase implements AccionBalanceoCommand {

    private static final int TEXTO_MAXIMO = 200;

    protected final AccionBalanceo accion;
    private final List<SnapshotZona> afectadas;
    private final AccionBalanceoRepository accionRepository;
    private final EventoDemandaRepository eventoRepository;

    protected AccionBalanceoBase(
            AccionBalanceo accion,
            List<SnapshotZona> afectadas,
            AccionBalanceoRepository accionRepository,
            EventoDemandaRepository eventoRepository) {

        this.accion = accion;
        this.afectadas = afectadas;
        this.accionRepository = accionRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public void ejecutar() {
        accionRepository.save(accion);
        registrarEventos(descripcion());
    }

    @Override
    public void deshacer() {
        registrarEventos("REVERSIÓN: " + descripcion());
    }

    private void registrarEventos(String texto) {
        String recortado = texto.length() > TEXTO_MAXIMO
                ? texto.substring(0, TEXTO_MAXIMO) : texto;
        Instant ahora = Instant.now();

        List<EventoDemanda> eventos = afectadas.stream().map(snapshot -> {
            EventoDemanda evento = new EventoDemanda();
            evento.setZona(snapshot.zona());
            evento.setFechaHora(ahora);
            evento.setDemandaKw(Potencia.kw(snapshot.demandaKw()));
            evento.setGeneracionKw(Potencia.kw(snapshot.generacionKw()));
            evento.setAccion(recortado);
            return evento;
        }).toList();

        eventoRepository.saveAll(eventos);
    }
}