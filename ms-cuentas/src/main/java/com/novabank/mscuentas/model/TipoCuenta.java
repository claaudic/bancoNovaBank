package com.novabank.mscuentas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TipoCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoCuenta;

    @Column(unique = true, nullable = false, length = 30)
    private String nombreTipoCuenta;

    @OneToMany(mappedBy = "tipoCuenta")
    private List<Cuenta> cuentas;
}
