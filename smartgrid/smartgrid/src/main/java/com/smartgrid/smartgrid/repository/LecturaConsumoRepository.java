package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.LecturaConsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface LecturaConsumoRepository
        extends JpaRepository<LecturaConsumo, Long> {

    List<LecturaConsumo> findByMedidorIdAndFechaHoraBetweenOrderByFechaHora(
            Long medidorId, Instant desde, Instant hasta);

    boolean existsByMedidorIdAndFechaHora(Long medidorId, Instant fechaHora);

    // Las sumas devuelven null cuando no hay lecturas; el servicio lo trata como 0.

    @Query("select sum(l.consumoKwh) from LecturaConsumo l "
            + "where l.medidor.id = :medidorId "
            + "and l.fechaHora between :desde and :hasta")
    BigDecimal sumaPorMedidor(@Param("medidorId") Long medidorId,
                              @Param("desde") Instant desde,
                              @Param("hasta") Instant hasta);

    @Query("select sum(l.consumoKwh) from LecturaConsumo l "
            + "where l.medidor.cliente.id = :clienteId "
            + "and l.fechaHora between :desde and :hasta")
    BigDecimal sumaPorCliente(@Param("clienteId") Long clienteId,
                              @Param("desde") Instant desde,
                              @Param("hasta") Instant hasta);

    @Query("select sum(l.consumoKwh) from LecturaConsumo l "
            + "where l.medidor.zona.id = :zonaId "
            + "and l.fechaHora between :desde and :hasta")
    BigDecimal sumaConsumoPorZona(@Param("zonaId") Long zonaId,
                                  @Param("desde") Instant desde,
                                  @Param("hasta") Instant hasta);

    /** Una sola consulta para todo el árbol (evita una consulta por zona). */
    @Query("select l.medidor.zona.id as zonaId, sum(l.consumoKwh) as total "
            + "from LecturaConsumo l "
            + "where l.fechaHora between :desde and :hasta "
            + "group by l.medidor.zona.id")
    List<ConsumoPorZona> consumoPorZona(@Param("desde") Instant desde,
                                        @Param("hasta") Instant hasta);

    /** Lecturas de todos los medidores de un cliente; rango semiabierto [desde, hasta). */
    @Query("select l from LecturaConsumo l "
            + "where l.medidor.cliente.id = :clienteId "
            + "and l.fechaHora >= :desde and l.fechaHora < :hasta "
            + "order by l.fechaHora")
    List<LecturaConsumo> lecturasClienteEnRango(
            @Param("clienteId") Long clienteId,
            @Param("desde") Instant desde,
            @Param("hasta") Instant hasta);
}