package com.novabank.mstransacciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransaccion;

    @Column(nullable = false)
    private Long idCuentaOrigen;

    @Column(nullable = false)
    private Long idCuentaDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoTransaccion tipoTransaccion;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montoTransaccion;

    @Column(nullable = false)
    private LocalDateTime fechaTransaccion;

    @Column(length = 35)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Estado estado;

}
