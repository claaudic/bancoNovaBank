package com.novabank.msorganizacion.dto.request;

import com.novabank.msorganizacion.model.DireccionSucursal;
import com.novabank.msorganizacion.model.TipoDireccion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionSucursalRequestDTO {

    @NotNull(message = "El tipo de direccion es obligatorio")
    private TipoDireccion tipoDireccion;

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 80, message = "La calle no puede superar los 80 caracteres")
    private String calle;

    @NotBlank(message = "El numero es obligatorio")
    @Size(max = 10, message = "El numero no puede superar los 10 caracteres")
    private String numero;

    @Size(max = 10, message = "El depto no puede superar los 10 caracteres")
    private String depto;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 50, message = "La ciudad no puede superar los 50 caracteres")
    private String ciudad;

    @NotBlank(message = "La region es obligatoria")
    @Size(max = 50, message = "La region no puede superar los 50 caracteres")
    private String region;

    @Size(max = 100, message = "La referencia no puede superar los 100 caracteres")
    private String referencia;

    @NotNull(message = "El id de la sucursal es obligatorio")
    private Long idSucursal;

    public DireccionSucursal toEntity() {
        DireccionSucursal d = new DireccionSucursal();
        d.setTipoDireccion(tipoDireccion);
        d.setCalle(calle);
        d.setNumero(numero);
        d.setDepto(depto);
        d.setCiudad(ciudad);
        d.setRegion(region);
        d.setReferencia(referencia);
        return d;
    }
}
