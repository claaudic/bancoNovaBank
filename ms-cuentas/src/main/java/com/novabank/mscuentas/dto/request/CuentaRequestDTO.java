package com.novabank.mscuentas.dto.request;

import com.novabank.mscuentas.model.Cuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CuentaRequestDTO {

    @NotBlank(message = "El numero de cuenta es obligatorio")
    @Pattern(regexp = "^[0-9]{8,20}$", message = "El numero de cuenta debe contener entre 8 y 20 digitos")
    private String numeroCuenta;

    @NotNull(message = "El saldo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo no puede ser negativo")
    private BigDecimal saldo;

    @NotBlank(message = "El rut del cliente es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9kK]$", message = "El rut debe tener formato 12345678-9")
    private String rutCliente;

    @NotNull(message = "El id del tipo de cuenta es obligatorio")
    private Long idTipoCuenta;

    public Cuenta toEntity() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(numeroCuenta);
        cuenta.setSaldo(saldo);
        cuenta.setRutCliente(rutCliente);
        return cuenta;
    }
}
