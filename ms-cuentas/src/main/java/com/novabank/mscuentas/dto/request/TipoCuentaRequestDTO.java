package com.novabank.mscuentas.dto.request;

import com.novabank.mscuentas.model.TipoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoCuentaRequestDTO {

    @NotBlank(message = "El nombre del tipo de cuenta es obligatorio")
    @Size(max = 30, message = "El nombre del tipo de cuenta no puede superar los 30 caracteres")
    private String nombreTipoCuenta;

    public TipoCuenta toEntity() {
        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombreTipoCuenta(nombreTipoCuenta);
        return tipoCuenta;
    }
}
