package com.novabank.msorganizacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionSucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDireccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoDireccion tipoDireccion;

    @Column(nullable = false, length = 80)
    private String calle;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(length = 10)
    private String depto;

    @Column(nullable = false, length = 50)
    private String ciudad;

    @Column(nullable = false, length = 50)
    private String region;

    @Column(length = 100)
    private String referencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;
}
