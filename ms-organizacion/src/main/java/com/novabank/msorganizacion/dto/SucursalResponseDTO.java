package com.novabank.msorganizacion.dto;

import com.novabank.msorganizacion.model.Sucursal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SucursalResponseDTO {
    private Long idSucursal;
    private String nombre;
    private String email;
    private String telefono;

    public static SucursalResponseDTO fromEntity(Sucursal s) {
        return new SucursalResponseDTO(
                s.getIdSucursal(),
                s.getNombre(),
                s.getEmail(),
                s.getTelefono()
        );
    }
}