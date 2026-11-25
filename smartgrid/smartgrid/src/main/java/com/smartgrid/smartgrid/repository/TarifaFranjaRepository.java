package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.TarifaFranja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarifaFranjaRepository
        extends JpaRepository<TarifaFranja, Long> {

    List<TarifaFranja> findByTarifaIdOrderByHoraInicio(Long tarifaId);
}