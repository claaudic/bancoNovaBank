package com.novabank.msclientes.dto;

import com.novabank.msclientes.model.Cliente;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

//Datos de salida
@Data
public class ClienteResponseDTO {

    private String rutCliente;
    private String numeroSerie;
    private String nombreCliente;
    private String apellidoCliente;
    private String telefonoCliente;
    private String emailCliente;
    private LocalDateTime fechaCreacion;
    private Integer estado;

    private ProfesionResponseDTO profesion;

    private List<DireccionClienteRequestDTO> direcciones;


    public static ClienteResponseDTO toResponseDTO(Cliente cliente) {
        ClienteResponseDTO response = new ClienteResponseDTO();


        return response;
    }
}
