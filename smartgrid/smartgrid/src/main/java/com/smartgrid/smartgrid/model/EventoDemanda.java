// EventoDemanda.java
package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "evento_demanda")
@Getter @Setter @NoArgsConstructor
public class EventoDemanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zona_id", nullable = false)
    private ZonaRed zona;

    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;

    @Column(name = "demanda_kw", nullable = false, precision = 14, scale = 3)
    private BigDecimal demandaKw;

    @Column(name = "generacion_kw", nullable = false, precision = 14, scale = 3)
    private BigDecimal generacionKw;

    @Column(length = 200)
    private String accion;
}