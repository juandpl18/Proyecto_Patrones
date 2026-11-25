package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tarifa_franja")
@Getter @Setter @NoArgsConstructor
public class TarifaFranja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tarifa_id", nullable = false)
    private Tarifa tarifa;

    @Column(nullable = false, length = 40)
    private String nombre;

    @Column(name = "hora_inicio", nullable = false)
    private int horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private int horaFin;

    @Column(name = "precio_kwh", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioKwh;
}