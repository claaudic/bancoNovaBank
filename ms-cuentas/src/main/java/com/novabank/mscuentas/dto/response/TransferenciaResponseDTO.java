package com.novabank.mscuentas.dto.response;

import com.novabank.mscuentas.model.Cuenta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferenciaResponseDTO {

    private CuentaResponseDTO cuentaOrigen;
    private CuentaResponseDTO cuentaDestino;
    private BigDecimal monto;
    private LocalDateTime fechaTransferencia;

    public static TransferenciaResponseDTO toResponseDTO(Cuenta origen, Cuenta destino, BigDecimal monto) {
        return new TransferenciaResponseDTO(
                CuentaResponseDTO.toResponseDTO(origen),
                CuentaResponseDTO.toResponseDTO(destino),
                monto,
                LocalDateTime.now()
        );
    }
}
