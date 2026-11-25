package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.Alerta;
import com.smartgrid.smartgrid.model.Severidad;
import com.smartgrid.smartgrid.model.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByAtendidaFalseOrderByCreadaEnDesc();

    /** Evita duplicar una alerta que todavía no fue atendida. */
    boolean existsByZonaIdAndTipoAndSeveridadAndAtendidaFalse(
            Long zonaId, TipoAlerta tipo, Severidad severidad);
}