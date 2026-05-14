package com.novabank.msorganizacion.dto;

import com.novabank.msorganizacion.model.DireccionSucursal;
import com.novabank.msorganizacion.model.TipoDireccion;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DireccionSucursalResponseDTO {
    private Long idDireccion;
    private TipoDireccion tipoDireccion;
    private String calle;
    private String numero;
    private String depto;
    private String ciudad;
    private String region;
    private Long idSucursal;

    public static DireccionSucursalResponseDTO fromEntity(DireccionSucursal d) {
        return new DireccionSucursalResponseDTO(
                d.getIdDireccion(),
                d.getTipoDireccion(),
                d.getCalle(),
                d.getNumero(),
                d.getDepto(),
                d.getCiudad(),
                d.getRegion(),
                d.getSucursal() != null ? d.getSucursal().getIdSucursal() : null
        );
    }
}