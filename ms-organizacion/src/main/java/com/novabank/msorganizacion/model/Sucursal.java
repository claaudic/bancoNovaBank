package com.novabank.msorganizacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSucursal;

    @Column(unique = true, nullable = false, length = 80)
    private String nombre;

    @Column(unique = true, nullable = false, length = 80)
    private String email;

    @Column(nullable = false, length = 15)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Estado estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    private List<DireccionSucursal> direcciones;

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    private List<Ejecutivo> ejecutivos;
}
