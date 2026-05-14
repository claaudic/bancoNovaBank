package com.novabank.mstransacciones.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CuentaResponseDTO {

    private Long idCuenta;
    private String numeroCuenta;
    private BigDecimal saldo;
    private String rutCliente;
    private String estado;
}
