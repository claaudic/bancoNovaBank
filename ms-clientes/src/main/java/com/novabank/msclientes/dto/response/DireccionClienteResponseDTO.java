package com.novabank.msclientes.dto.response;

import com.novabank.msclientes.model.DireccionCliente;
import com.novabank.msclientes.model.TipoDireccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionClienteResponseDTO {

    private String calle;
    private String numero;
    private String depta;
    private TipoDireccion tipoDireccion;
    private String ciudad;

    public static DireccionClienteResponseDTO toDireccionClienteResponseDTO(DireccionCliente direccion) {
        return new DireccionClienteResponseDTO(
                direccion.getCalle(),
                direccion.getNumero(),
                direccion.getDepta(),
                direccion.getTipoDireccion(),
                direccion.getCiudad()
        );
    }
}
