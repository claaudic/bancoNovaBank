package com.novabank.mstransacciones.dto;

import com.novabank.mstransacciones.model.TipoTransaccion;
import com.novabank.mstransacciones.model.Transaccion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransaccionRequestDTO {

    @NotNull(message = "La cuenta de origen es obligatoria")
    private Long idCuentaOrigen;

    @NotNull(message = "La cuenta de destino es obligatoria")
    private Long idCuentaDestino;

    @NotBlank(message = "El tipo de transaccion no puede estar en blanco")
    private TipoTransaccion tipoTransaccion;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto a transferir debe ser mayor a 0")
    private BigDecimal montoTransaccion;

    @Size(max = 35, message = "La descripcion no puede superar los 35 caracteres")
    private String descripcion;

    public Transaccion transaccionEntity() {
        Transaccion transaccion = new Transaccion();
        transaccion.setIdCuentaOrigen(idCuentaOrigen);
        transaccion.setIdCuentaDestino(idCuentaDestino);
        transaccion.setTipoTransaccion(tipoTransaccion);
        transaccion.setMontoTransaccion(montoTransaccion);
        transaccion.setDescripcion(descripcion);
        return transaccion;
    }
}
