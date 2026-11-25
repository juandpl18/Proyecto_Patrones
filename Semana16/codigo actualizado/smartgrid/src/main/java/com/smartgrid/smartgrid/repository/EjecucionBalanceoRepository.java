package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.EjecucionBalanceo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EjecucionBalanceoRepository
        extends JpaRepository<EjecucionBalanceo, Long> {

    List<EjecucionBalanceo> findAllByOrderByEjecutadaEnDesc();

    List<EjecucionBalanceo> findByZonaIdOrderByEjecutadaEnDesc(Long zonaId);
}