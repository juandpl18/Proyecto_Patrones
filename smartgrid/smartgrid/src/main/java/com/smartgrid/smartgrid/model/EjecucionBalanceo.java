package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ejecucion_balanceo")
@Getter @Setter @NoArgsConstructor
public class EjecucionBalanceo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zona_id", nullable = false)
    private ZonaRed zona;

    @Column(nullable = false, length = 30)
    private String estrategia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEjecucion estado = EstadoEjecucion.EJECUTADA;

    @Column(name = "instante_referencia", nullable = false)
    private Instant instanteReferencia;

    @Column(name = "irradiancia_wm2", nullable = false, precision = 10, scale = 3)
    private BigDecimal irradianciaWm2;

    @Column(name = "viento_ms", nullable = false, precision = 10, scale = 3)
    private BigDecimal vientoMs;

    @Column(name = "factor_disponibilidad", nullable = false,
            precision = 10, scale = 3)
    private BigDecimal factorDisponibilidad;

    @Column(name = "ejecutada_por", nullable = false)
    private String ejecutadaPor;

    @Column(name = "ejecutada_en", nullable = false, updatable = false)
    private Instant ejecutadaEn;

    @Column(name = "revertida_por")
    private String revertidaPor;

    @Column(name = "revertida_en")
    private Instant revertidaEn;

    @PrePersist
    void antesDeGuardar() {
        if (ejecutadaEn == null) ejecutadaEn = Instant.now();
    }
}