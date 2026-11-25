package com.smartgrid.smartgrid.command.balanceo;

import com.smartgrid.smartgrid.balanceo.ZonaAnalizada;
import com.smartgrid.smartgrid.model.AccionBalanceo;
import com.smartgrid.smartgrid.repository.AccionBalanceoRepository;
import com.smartgrid.smartgrid.repository.EventoDemandaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class AccionBalanceoFactory {

    private final AccionBalanceoRepository accionRepository;
    private final EventoDemandaRepository eventoRepository;

    public AccionBalanceoFactory(
            AccionBalanceoRepository accionRepository,
            EventoDemandaRepository eventoRepository) {

        this.accionRepository = accionRepository;
        this.eventoRepository = eventoRepository;
    }

    public List<AccionBalanceoCommand> comandos(
            List<AccionBalanceo> acciones,
            Map<Long, ZonaAnalizada> zonas) {

        return acciones.stream()
                .map(accion -> crear(accion, zonas))
                .toList();
    }

    private AccionBalanceoCommand crear(
            AccionBalanceo accion, Map<Long, ZonaAnalizada> zonas) {

        return switch (accion.getTipo()) {
            case TRANSFERENCIA -> new TransferirCargaCommand(
                    accion,
                    snapshots(zonas,
                            accion.getZonaOrigen().getId(),
                            accion.getZona().getId()),
                    accionRepository, eventoRepository);

            case DESLASTRE -> new DeslastreCommand(
                    accion,
                    snapshots(zonas, accion.getZona().getId()),
                    accionRepository, eventoRepository);
        };
    }

    private List<SnapshotZona> snapshots(
            Map<Long, ZonaAnalizada> zonas, Long... ids) {

        return Stream.of(ids)
                .map(zonas::get)
                .filter(Objects::nonNull)
                .map(z -> new SnapshotZona(
                        z.zona(), z.demandaKw(), z.generacionKw()))
                .toList();
    }
}