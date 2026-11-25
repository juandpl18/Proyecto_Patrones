package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "factura")
@Getter @Setter @NoArgsConstructor
public class FacturaEmitida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tarifa_id", nullable = false)
    private Tarifa tarifa;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    @Column(name = "consumo_kwh", nullable = false, precision = 14, scale = 3)
    private BigDecimal consumoKwh;

    /** Precio promedio por kWh (energía / consumo). */
    @Column(name = "precio_kwh", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioKwh;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal energia;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoFactura estado = EstadoFactura.EMITIDA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModoFacturacion modo = ModoFacturacion.LECTURAS;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "emitida_en", nullable = false, updatable = false)
    private Instant emitidaEn;

    @Column(name = "emitida_por")
    private String emitidaPor;

    @Column(name = "pagada_en")
    private Instant pagadaEn;

    @Column(name = "anulada_en")
    private Instant anuladaEn;

    @Column(name = "motivo_anulacion", length = 200)
    private String motivoAnulacion;

    @PrePersist
    void antesDeGuardar() {
        if (emitidaEn == null) emitidaEn = Instant.now();
    }
}