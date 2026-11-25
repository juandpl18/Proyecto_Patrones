package com.smartgrid.smartgrid.command.balanceo;

import com.smartgrid.smartgrid.model.AccionBalanceo;
import com.smartgrid.smartgrid.repository.AccionBalanceoRepository;
import com.smartgrid.smartgrid.repository.EventoDemandaRepository;

import java.util.List;
import java.util.Locale;

public class TransferirCargaCommand extends AccionBalanceoBase {

    public TransferirCargaCommand(
            AccionBalanceo accion,
            List<SnapshotZona> afectadas,
            AccionBalanceoRepository accionRepository,
            EventoDemandaRepository eventoRepository) {

        super(accion, afectadas, accionRepository, eventoRepository);
    }

    @Override
    public String descripcion() {
        return String.format(Locale.ROOT,
                "TRANSFERENCIA de %.3f kW desde '%s' hacia '%s' "
                        + "(ejecución #%d, %s)",
                accion.getKw().doubleValue(),
                accion.getZonaOrigen().getNombre(),
                accion.getZona().getNombre(),
                accion.getEjecucion().getId(),
                accion.getEjecucion().getEstrategia());
    }
}