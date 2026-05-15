package com.novabank.msorganizacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ejecutivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEjecutivo;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String apellido;

    @Column(unique = true, nullable = false, length = 80)
    private String email;

    @Column(nullable = false, length = 15)
    private String telefono;

    @Column(nullable = false, length = 40)
    private String cargo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Estado estado;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;
}
