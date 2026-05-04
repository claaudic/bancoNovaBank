package com.novabank.msclientes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cliente {

    @Id
    @Column(unique = true,nullable = false, length = 12)
    private String rutCliente;

    @Column(unique = true,nullable = false, length = 9)
    private String numeroSerie;

    @Column(nullable = false, length = 50)
    private String nombreCliente;

    @Column(nullable = false, length = 50)
    private String apellidoCliente;

    @Column(nullable = false, length = 15)
    private String telefonoCliente;

    @Column(nullable = true, length = 50)
    private String emailCliente;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private Estado estado;

    // uno a muchos, quiere decir que un cliente puede tener muchas direcciones
    @OneToMany(mappedBy = "cliente")
    private List<DireccionCliente> direccionClientes;

    // muchos a uno, quiere decir que un cliente solo puede tener una profesion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesion")
    private Profesion profesion;
}
