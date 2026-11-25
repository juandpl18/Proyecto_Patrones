package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "lectura_consumo")
@Getter @Setter @NoArgsConstructor
public class LecturaConsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medidor_id", nullable = false)
    private Medidor medidor;

    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;

    @Column(name = "consumo_kwh", nullable = false, precision = 14, scale = 3)
    private BigDecimal consumoKwh;
}