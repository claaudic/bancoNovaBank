package com.novabank.msorganizacion.dto;

import com.novabank.msorganizacion.model.Sucursal;
import lombok.Data;

@Data
public class SucursalRequestDTO {
    private String nombre;
    private String email;
    private String telefono;

    public Sucursal toEntity() {
        Sucursal s = new Sucursal();
        s.setNombre(this.nombre);
        s.setEmail(this.email);
        s.setTelefono(this.telefono);
        return s;
    }
}
