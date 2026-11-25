package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "zona_red")
@Getter @Setter @NoArgsConstructor
public class ZonaRed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoZona tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_padre_id")
    private ZonaRed zonaPadre;

    @Column(name = "capacidad_kw", precision = 14, scale = 3)
    private BigDecimal capacidadKw;
}