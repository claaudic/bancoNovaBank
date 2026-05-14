package com.novabank.mscuentas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarjeta;

    @Column(unique = true, nullable = false, length = 19)
    private String numeroTarjeta;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, length = 4)
    private String cvv;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoTarjeta estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta", nullable = false)
    private Cuenta cuenta;
}
