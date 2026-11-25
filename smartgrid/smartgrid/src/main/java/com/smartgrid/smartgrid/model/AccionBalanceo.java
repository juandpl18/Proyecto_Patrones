package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "accion_balanceo")
@Getter @Setter @NoArgsConstructor
public class AccionBalanceo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ejecucion_id", nullable = false)
    private EjecucionBalanceo ejecucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAccionBalanceo tipo;

    /** Solo en transferencias: la zona que cede. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_origen_id")
    private ZonaRed zonaOrigen;

    /** Transferencia: la zona que recibe. Deslastre: la zona afectada. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zona_id", nullable = false)
    private ZonaRed zona;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal kw;
}