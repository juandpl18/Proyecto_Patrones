package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.TarifaTramo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarifaTramoRepository
        extends JpaRepository<TarifaTramo, Long> {

    List<TarifaTramo> findByTarifaIdOrderByDesdeKwh(Long tarifaId);
}