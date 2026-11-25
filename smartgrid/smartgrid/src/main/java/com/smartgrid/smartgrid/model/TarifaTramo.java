package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tarifa_tramo")
@Getter @Setter @NoArgsConstructor
public class TarifaTramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tarifa_id", nullable = false)
    private Tarifa tarifa;

    @Column(name = "desde_kwh", nullable = false, precision = 14, scale = 3)
    private BigDecimal desdeKwh;

    /** Null en el último tramo (sin límite superior). */
    @Column(name = "hasta_kwh", precision = 14, scale = 3)
    private BigDecimal hastaKwh;

    @Column(name = "precio_kwh", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioKwh;
}