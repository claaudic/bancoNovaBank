package com.novabank.mscuentas.dto.response;

import com.novabank.mscuentas.model.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoCuentaResponseDTO {

    private Long idTipoCuenta;
    private String nombreTipoCuenta;

    public static TipoCuentaResponseDTO toResponseDTO(TipoCuenta tipoCuenta) {
        return new TipoCuentaResponseDTO(
                tipoCuenta.getIdTipoCuenta(),
                tipoCuenta.getNombreTipoCuenta()
        );
    }
}
