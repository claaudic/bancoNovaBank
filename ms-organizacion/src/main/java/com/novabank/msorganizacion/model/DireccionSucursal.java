package com.novabank.msorganizacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionSucursal {

    @Id
    @Column(unique = true,nullable = false, length = 30)
    private Long idDireccion;

    @Column(nullable = false,length = 12)
    private TipoDireccion tipoDireccion;

    @Column(nullable = false,length = 50)
    private String calle;

    @Column(nullable = false,length = 5)
    private String numero;

    @Column(nullable = false,length = 5)
    private String depto;

    @Column(nullable = false,length = 50)
    private String ciudad;

    @Column(nullable = false,length = 30)
    private String region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Sucursal")
    private Sucursal sucursal;

}