package com.novabank.msorganizacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSucursal;

    private String nombre;
    private String email;
    private String telefono;


    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    private List<DireccionSucursal> direcciones;

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    private List<Ejecutivo> ejecutivos;
}
