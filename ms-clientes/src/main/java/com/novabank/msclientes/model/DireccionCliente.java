package com.novabank.msclientes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DireccionCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 50)
    private String calle;

    @Column(nullable = false,length = 5)
    private String numero;

    @Column(nullable = false,length = 5)
    private String depta;

    @Column(nullable = false,length = 12)
    private TipoDireccion tipoDireccion;

    @Column(nullable = false,length = 50)
    private String ciudad;

    // muchos a uno, quiere decir que una direccion solo puede pertenecer a un cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rut_cliente")
    private Cliente cliente;
}
