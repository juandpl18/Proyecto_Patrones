package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.ZonaRed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZonaRedRepository extends JpaRepository<ZonaRed, Long> {

    List<ZonaRed> findByZonaPadreId(Long zonaPadreId);
}