package com.novabank.msorganizacion.dto;

import com.novabank.msorganizacion.model.Ejecutivo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EjecutivoResponseDTO {
    private Long idEjecutivo;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Long idSucursal;

    public static EjecutivoResponseDTO fromEntity(Ejecutivo e) {
        return new EjecutivoResponseDTO(
                e.getIdEjecutivo(),
                e.getNombre(),
                e.getApellido(),
                e.getEmail(),
                e.getTelefono(),
                e.getSucursal() != null ? e.getSucursal().getIdSucursal() : null
        );
    }
}