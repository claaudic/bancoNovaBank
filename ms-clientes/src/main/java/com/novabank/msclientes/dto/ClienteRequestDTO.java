package com.novabank.msclientes.dto;

import com.novabank.msclientes.model.Cliente;
import com.novabank.msclientes.model.Profesion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

//Datos de entrada
@Data
public class ClienteRequestDTO {

    @NotBlank
    private String rutCliente;

    @NotBlank
    private String numeroSerie;

    @NotBlank
    private String nombreCliente;

    @NotBlank
    private String apellidoCliente;

    @NotBlank
    private String telefonoCliente;

    @Email
    private String emailCliente;

    @NotNull
    private Long idProfesion;

    private List<DireccionClienteRequestDTO> direcciones;

    public Cliente toEntity() {
        Cliente cliente = new Cliente();
        cliente.setRutCliente(rutCliente);
        cliente.setNumeroSerie(numeroSerie);
        cliente.setNombreCliente(nombreCliente);
        cliente.setApellidoCliente(apellidoCliente);
        cliente.setTelefonoCliente(telefonoCliente);
        cliente.setEmailCliente(emailCliente);
        return cliente;
    }
}
