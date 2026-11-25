package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.EstadoFactura;
import com.smartgrid.smartgrid.model.FacturaEmitida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FacturaEmitidaRepository
        extends JpaRepository<FacturaEmitida, Long> {

    List<FacturaEmitida> findByClienteIdOrderByEmitidaEnDesc(Long clienteId);

    List<FacturaEmitida> findAllByOrderByEmitidaEnDesc();

    List<FacturaEmitida> findByEstadoOrderByEmitidaEnDesc(EstadoFactura estado);

    List<FacturaEmitida> findByClienteIdAndEstadoOrderByEmitidaEnDesc(
            Long clienteId, EstadoFactura estado);

    boolean existsByClienteIdAndPeriodoInicioAndPeriodoFinAndEstadoNot(
            Long clienteId, LocalDate periodoInicio, LocalDate periodoFin,
            EstadoFactura estado);

    /** Base de la facturación estimada: las últimas 3 facturas no anuladas. */
    List<FacturaEmitida> findTop3ByClienteIdAndEstadoNotOrderByPeriodoFinDesc(
            Long clienteId, EstadoFactura estado);

    List<FacturaEmitida> findByEstadoAndFechaVencimientoBefore(
            EstadoFactura estado, LocalDate fecha);
}