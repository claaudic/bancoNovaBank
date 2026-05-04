package com.novabank.msclientes.dto;

import com.novabank.msclientes.model.Cliente;
import com.novabank.msclientes.model.DireccionCliente;
import com.novabank.msclientes.model.TipoDireccion;
import lombok.Data;

@Data
public class DireccionClienteRequestDTO {

    private String calle;

    private String numero;

    private String depta;

    private TipoDireccion tipoDireccion;

    private String ciudad;


    public DireccionCliente toEntity() {
        return new DireccionCliente(null,
                calle,
                numero,
                depta,
                tipoDireccion,
                ciudad,
                null);
    }
}
