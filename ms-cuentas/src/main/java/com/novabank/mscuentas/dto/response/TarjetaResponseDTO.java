package com.novabank.mscuentas.dto.response;

import com.novabank.mscuentas.model.EstadoTarjeta;
import com.novabank.mscuentas.model.Tarjeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TarjetaResponseDTO {

    private Long idTarjeta;
    private String numeroTarjeta;
    private LocalDate fechaVencimiento;
    private String cvv;
    private EstadoTarjeta estado;
    private Long idCuenta;

    public static TarjetaResponseDTO toResponseDTO(Tarjeta tarjeta) {
        TarjetaResponseDTO response = new TarjetaResponseDTO();
        response.setIdTarjeta(tarjeta.getIdTarjeta());
        response.setNumeroTarjeta(tarjeta.getNumeroTarjeta());
        response.setFechaVencimiento(tarjeta.getFechaVencimiento());
        response.setCvv(tarjeta.getCvv());
        response.setEstado(tarjeta.getEstado());
        response.setIdCuenta(tarjeta.getCuenta().getIdCuenta());
        return response;
    }
}
