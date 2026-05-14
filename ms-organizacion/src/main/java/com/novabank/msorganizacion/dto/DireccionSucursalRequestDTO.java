package com.novabank.msorganizacion.dto;

import com.novabank.msorganizacion.model.TipoDireccion;
import lombok.Data;

@Data
public class DireccionSucursalRequestDTO {
    private TipoDireccion tipoDireccion;
    private String calle;
    private String numero;
    private String depto;
    private String ciudad;
    private String region;
    private Long idSucursal;
}