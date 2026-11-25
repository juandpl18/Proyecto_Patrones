package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.EventoDemanda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoDemandaRepository
        extends JpaRepository<EventoDemanda, Long> {

    List<EventoDemanda> findByZonaIdOrderByFechaHoraDesc(Long zonaId);
}