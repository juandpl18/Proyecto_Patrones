package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.Tarifa;
import com.smartgrid.smartgrid.model.TipoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    Optional<Tarifa> findFirstByTipoClienteAndActivaTrueOrderByVigenteDesdeDesc(
            TipoCliente tipoCliente);

    /** Tarifas activas de un tipo de cliente, la más reciente primero. */
    List<Tarifa> findByTipoClienteAndActivaTrueOrderByVigenteDesdeDesc(
            TipoCliente tipoCliente);

    /** Tarifa vigente en una fecha. */
    @Query("select t from Tarifa t "
            + "where t.tipoCliente = :tipo and t.activa = true "
            + "and t.vigenteDesde <= :fecha "
            + "and (t.vigenteHasta is null or t.vigenteHasta >= :fecha) "
            + "order by t.vigenteDesde desc")
    List<Tarifa> vigentesEn(@Param("tipo") TipoCliente tipo,
                            @Param("fecha") LocalDate fecha);
}