package com.novabank.mscuentas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCuenta;

    @Column(unique = true, nullable = false, length = 20)
    private String numeroCuenta;

    @Column(nullable = false)
    private LocalDate fechaCreacion;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false, length = 12)
    private String rutCliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoCuenta estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_cuenta", nullable = false)
    private TipoCuenta tipoCuenta;
}
