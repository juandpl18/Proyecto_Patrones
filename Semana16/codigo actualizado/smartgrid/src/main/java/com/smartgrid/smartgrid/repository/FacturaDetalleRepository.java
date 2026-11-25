package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.FacturaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacturaDetalleRepository
        extends JpaRepository<FacturaDetalle, Long> {

    List<FacturaDetalle> findByFacturaIdOrderByOrden(Long facturaId);
}