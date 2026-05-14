package com.novabank.msorganizacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ejecutivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEjecutivo;

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "id_sucursal")
    private Sucursal sucursal;
}