package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "cambio_estado_planta")
@Getter @Setter @NoArgsConstructor
public class CambioEstadoPlanta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "planta_id", nullable = false)
    private PlantaGeneracion planta;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", nullable = false, length = 30)
    private EstadoPlanta estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 30)
    private EstadoPlanta estadoNuevo;

    @Column(length = 300)
    private String motivo;

    @Column(nullable = false)
    private String usuario;

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private Instant fechaHora;

    @PrePersist
    void antesDeGuardar() {
        if (fechaHora == null) fechaHora = Instant.now();
    }
}