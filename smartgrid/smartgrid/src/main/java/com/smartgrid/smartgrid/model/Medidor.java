// Medidor.java
package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "medidor")
@Getter @Setter @NoArgsConstructor
public class Medidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zona_id", nullable = false)
    private ZonaRed zona;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "instalado_en", nullable = false, updatable = false)
    private Instant instaladoEn;

    @PrePersist
    void antesDeGuardar() {
        if (instaladoEn == null) instaladoEn = Instant.now();
    }
}