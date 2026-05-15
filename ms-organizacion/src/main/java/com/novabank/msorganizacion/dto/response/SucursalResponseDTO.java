package com.novabank.msorganizacion.dto.response;

import com.novabank.msorganizacion.model.Estado;
import com.novabank.msorganizacion.model.Sucursal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SucursalResponseDTO {

    private Long idSucursal;
    private String nombre;
    private String email;
    private String telefono;
    private Estado estado;
    private LocalDateTime fechaCreacion;

    public static SucursalResponseDTO toResponseDTO(Sucursal s) {
        return new SucursalResponseDTO(
                s.getIdSucursal(),
                s.getNombre(),
                s.getEmail(),
                s.getTelefono(),
                s.getEstado(),
                s.getFechaCreacion()
        );
    }
}
