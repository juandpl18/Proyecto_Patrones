package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.AccionBalanceo;
import com.smartgrid.smartgrid.model.EstadoEjecucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AccionBalanceoRepository
        extends JpaRepository<AccionBalanceo, Long> {

    List<AccionBalanceo> findByEjecucionId(Long ejecucionId);

    /** Acciones cuyo efecto sigue vigente en el instante consultado. */
    @Query("select a from AccionBalanceo a "
            + "where a.ejecucion.estado = :estado "
            + "and a.ejecucion.instanteReferencia > :desde "
            + "and a.ejecucion.instanteReferencia <= :hasta")
    List<AccionBalanceo> vigentes(@Param("estado") EstadoEjecucion estado,
                                  @Param("desde") Instant desde,
                                  @Param("hasta") Instant hasta);
}