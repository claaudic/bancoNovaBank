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
    private TipoDireccion tipoDireccion;

    private String calle;
    private String numero;
    private String depto;
    private String ciudad;
    private String region;

    @ManyToOne
    @JoinColumn(name = "id_sucursal")
    private Sucursal sucursal;
}