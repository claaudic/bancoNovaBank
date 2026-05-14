package com.novabank.mscuentas.dto.request;

import com.novabank.mscuentas.model.Tarjeta;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TarjetaRequestDTO {

    @NotBlank(message = "El numero de tarjeta es obligatorio")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "El numero de tarjeta debe contener entre 13 y 19 digitos")
    private String numeroTarjeta;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimiento;

    @NotBlank(message = "El cvv es obligatorio")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "El cvv debe contener 3 o 4 digitos")
    private String cvv;

    @NotNull(message = "El id de la cuenta es obligatorio")
    private Long idCuenta;

    public Tarjeta toEntity() {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setNumeroTarjeta(numeroTarjeta);
        tarjeta.setFechaVencimiento(fechaVencimiento);
        tarjeta.setCvv(cvv);
        return tarjeta;
    }
}
