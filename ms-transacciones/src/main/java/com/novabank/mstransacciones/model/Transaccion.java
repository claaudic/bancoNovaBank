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

    @Column(unique = true)
    private Long idCuentaOrigen;

    @Column(unique = true)
    private Long idCuentaDestino;

    @Column(nullable = false, length = 13)
    private TipoTransaccion tipoTransaccion;

    @Column(nullable = false, length = 7)
    private BigDecimal montoTransaccion;

    @Column(nullable = false)
    private LocalDateTime fechaTransaccion;

    @Column(length = 35)
    private String descripcion;

    @Column(nullable = false)
    private Estado estado;

}
