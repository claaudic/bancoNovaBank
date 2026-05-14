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
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Estado estado = Estado.ACTIVO;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<DireccionCliente> direccionClientes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesion")
    private Profesion profesion;
}
