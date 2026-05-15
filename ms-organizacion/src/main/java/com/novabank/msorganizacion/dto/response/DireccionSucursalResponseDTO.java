package com.novabank.msorganizacion.dto.response;

import com.novabank.msorganizacion.model.DireccionSucursal;
import com.novabank.msorganizacion.model.TipoDireccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionSucursalResponseDTO {

    private Long idDireccion;
    private TipoDireccion tipoDireccion;
    private String calle;
    private String numero;
    private String depto;
    private String ciudad;
    private String region;
    private String referencia;
    private Long idSucursal;

    public static DireccionSucursalResponseDTO toResponseDTO(DireccionSucursal d) {
        return new DireccionSucursalResponseDTO(
                d.getIdDireccion(),
                d.getTipoDireccion(),
                d.getCalle(),
                d.getNumero(),
                d.getDepto(),
                d.getCiudad(),
                d.getRegion(),
                d.getReferencia(),
                d.getSucursal() != null ? d.getSucursal().getIdSucursal() : null
        );
    }
}
