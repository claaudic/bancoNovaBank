package com.novabank.msorganizacion.dto;

import lombok.Data;

@Data
public class EjecutivoRequestDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    private Long idSucursal;
}