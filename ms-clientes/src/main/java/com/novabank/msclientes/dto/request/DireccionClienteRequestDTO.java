package com.novabank.msclientes.dto.request;

import com.novabank.msclientes.model.DireccionCliente;
import com.novabank.msclientes.model.TipoDireccion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionClienteRequestDTO {

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 50, message = "La calle no puede superar los 50 caracteres")
    private String calle;

    @NotBlank(message = "El numero es obligatorio")
    @Size(max = 5, message = "El numero no puede superar los 5 caracteres")
    private String numero;

    @NotBlank(message = "El departamento es obligatorio")
    @Size(max = 5, message = "El departamento no puede superar los 5 caracteres")
    private String depta;

    @NotNull(message = "El tipo de direccion es obligatorio")
    private TipoDireccion tipoDireccion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 50, message = "La ciudad no puede superar los 50 caracteres")
    private String ciudad;


    public DireccionCliente toEntity() {
        return new DireccionCliente(null,
                calle,
                numero,
                depta,
                tipoDireccion,
                ciudad,
                null);
    }
}
