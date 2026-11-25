package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "factura_detalle")
@Getter @Setter @NoArgsConstructor
public class FacturaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "factura_id", nullable = false)
    private FacturaEmitida factura;

    @Column(nullable = false)
    private int orden;

    @Column(nullable = false, length = 150)
    private String concepto;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal valor;
}