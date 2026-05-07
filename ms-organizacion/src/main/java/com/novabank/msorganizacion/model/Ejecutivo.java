package com.novabank.msorganizacion.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ejecutivo {
    @Id
    @Column(unique = true,nullable = false, length = 30)
    private Long idEjecutivo;

    @Column(nullable = false,length = 50)
    private String nombre;

    @Column(nullable = false,length = 50)
    private String apellido;

    @Column(nullable = false,length = 50)
    private String email;

    @Column(nullable = false,length = 9)
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Sucursal")
    private Sucursal sucursal;
}
