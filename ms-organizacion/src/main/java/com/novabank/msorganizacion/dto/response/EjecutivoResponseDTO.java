package com.novabank.msorganizacion.dto.response;

import com.novabank.msorganizacion.model.Ejecutivo;
import com.novabank.msorganizacion.model.Estado;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EjecutivoResponseDTO {

    private Long idEjecutivo;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String cargo;
    private Estado estado;
    private LocalDate fechaIngreso;
    private Long idSucursal;

    public static EjecutivoResponseDTO toResponseDTO(Ejecutivo e) {
        return new EjecutivoResponseDTO(
                e.getIdEjecutivo(),
                e.getNombre(),
                e.getApellido(),
                e.getEmail(),
                e.getTelefono(),
                e.getCargo(),
                e.getEstado(),
                e.getFechaIngreso(),
                e.getSucursal() != null ? e.getSucursal().getIdSucursal() : null
        );
    }
}
