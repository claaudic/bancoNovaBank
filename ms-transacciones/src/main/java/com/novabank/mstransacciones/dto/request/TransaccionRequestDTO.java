package com.novabank.mstransacciones.dto.request;

import com.novabank.mstransacciones.model.TipoTransaccion;
import com.novabank.mstransacciones.model.Transaccion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionRequestDTO {

    @NotNull(message = "La cuenta de origen es obligatoria")
    private Long idCuentaOrigen;

    @NotNull(message = "La cuenta de destino es obligatoria")
    private Long idCuentaDestino;

    @NotNull(message = "El tipo de transaccion es obligatorio")
    private TipoTransaccion tipoTransaccion;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    @Digits(integer = 17, fraction = 2, message = "El monto debe tener hasta 17 enteros y 2 decimales")
    private BigDecimal montoTransaccion;

    @Size(max = 35, message = "La descripcion no puede superar los 35 caracteres")
    private String descripcion;

    public Transaccion toEntity() {
        Transaccion transaccion = new Transaccion();
        transaccion.setIdCuentaOrigen(idCuentaOrigen);
        transaccion.setIdCuentaDestino(idCuentaDestino);
        transaccion.setTipoTransaccion(tipoTransaccion);
        transaccion.setMontoTransaccion(montoTransaccion);
        transaccion.setDescripcion(descripcion);
        return transaccion;
    }
}
