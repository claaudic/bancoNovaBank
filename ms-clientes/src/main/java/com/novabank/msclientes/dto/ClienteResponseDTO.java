package com.novabank.msclientes.dto;

import com.novabank.msclientes.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

//Datos de salida
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private List<DireccionClienteResponseDTO> direcciones;

    public static ClienteResponseDTO toResponseDTO(Cliente cliente) {
        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setRutCliente(cliente.getRutCliente());
        response.setNumeroSerie(cliente.getNumeroSerie());
        response.setNombreCliente(cliente.getNombreCliente());
        response.setApellidoCliente(cliente.getApellidoCliente());
        response.setTelefonoCliente(cliente.getTelefonoCliente());
        response.setEmailCliente(cliente.getEmailCliente());
        response.setFechaCreacion(cliente.getFechaCreacion());
        response.setProfesion(ProfesionResponseDTO.toProfesionResponseDTO(cliente.getProfesion()));
        response.setDirecciones(cliente.getDireccionClientes().stream().map(DireccionClienteResponseDTO::toDireccionClienteResponseDTO).toList());

        return response;
    }
}
