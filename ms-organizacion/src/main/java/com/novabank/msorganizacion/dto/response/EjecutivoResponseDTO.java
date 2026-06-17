package com.novabank.msorganizacion.dto.response;

import com.novabank.msorganizacion.model.Ejecutivo;
import com.novabank.msorganizacion.model.Estado;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EjecutivoResponseDTO extends RepresentationModel<EjecutivoResponseDTO> {

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
