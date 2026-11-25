// PlantaGeneracion.java
package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "planta_generacion")
@Getter @Setter @NoArgsConstructor
public class PlantaGeneracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_fuente", nullable = false, length = 20)
    private TipoFuente tipoFuente;

    @Column(name = "capacidad_kw", nullable = false, precision = 14, scale = 3)
    private BigDecimal capacidadKw;

    @Column(name = "voltaje_salida", nullable = false, precision = 10, scale = 2)
    private BigDecimal voltajeSalida;

    @Column(name = "tiene_bateria_reserva", nullable = false)
    private boolean tieneBateriaReserva;

    @Column(nullable = false, length = 200)
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPlanta estado = EstadoPlanta.ACTIVA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_id")
    private ZonaRed zona;

    @Column(name = "creado_por")
    private String creadoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @PrePersist
    void antesDeGuardar() {
        if (creadoEn == null) creadoEn = Instant.now();
    }
}