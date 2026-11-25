package com.smartgrid.smartgrid.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "cargo_adicional")
@Getter @Setter @NoArgsConstructor
public class CargoAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    /** Null = aplica a todos los tipos de cliente. */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", length = 20)
    private TipoCliente tipoCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_calculo", nullable = false, length = 20)
    private TipoCalculoCargo tipoCalculo;

    /** PORCENTAJE: % sobre la energía (negativo = descuento). FIJO: valor en pesos. */
    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal valor;

    @Column(nullable = false)
    private boolean activo = true;
}