package com.novabank.mstransacciones.dto.response;

import com.novabank.mstransacciones.model.Estado;
import com.novabank.mstransacciones.model.TipoTransaccion;
import com.novabank.mstransacciones.model.Transaccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransaccionResponseDTO {

    private Long idTransaccion;
    private Long idCuentaOrigen;
    private Long idCuentaDestino;
    private TipoTransaccion tipoTransaccion;
    private BigDecimal montoTransaccion;
    private LocalDateTime fechaTransaccion;
    private String descripcion;
    private Estado estado;

    public static TransaccionResponseDTO toResponseDTO(Transaccion transaccion) {
        TransaccionResponseDTO response = new TransaccionResponseDTO();
        response.setIdTransaccion(transaccion.getIdTransaccion());
        response.setIdCuentaOrigen(transaccion.getIdCuentaOrigen());
        response.setIdCuentaDestino(transaccion.getIdCuentaDestino());
        response.setTipoTransaccion(transaccion.getTipoTransaccion());
        response.setMontoTransaccion(transaccion.getMontoTransaccion());
        response.setFechaTransaccion(transaccion.getFechaTransaccion());
        response.setDescripcion(transaccion.getDescripcion());
        response.setEstado(transaccion.getEstado());
        return response;
    }
}
