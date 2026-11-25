package com.smartgrid.smartgrid.command.balanceo;

import com.smartgrid.smartgrid.model.AccionBalanceo;
import com.smartgrid.smartgrid.repository.AccionBalanceoRepository;
import com.smartgrid.smartgrid.repository.EventoDemandaRepository;

import java.util.List;
import java.util.Locale;

public class DeslastreCommand extends AccionBalanceoBase {

    public DeslastreCommand(
            AccionBalanceo accion,
            List<SnapshotZona> afectadas,
            AccionBalanceoRepository accionRepository,
            EventoDemandaRepository eventoRepository) {

        super(accion, afectadas, accionRepository, eventoRepository);
    }

    @Override
    public String descripcion() {
        return String.format(Locale.ROOT,
                "DESLASTRE de %.3f kW en '%s' (ejecución #%d)",
                accion.getKw().doubleValue(),
                accion.getZona().getNombre(),
                accion.getEjecucion().getId());
    }
}