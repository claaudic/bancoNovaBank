package com.novabank.msclientes.dto.request;

import com.novabank.msclientes.model.Cliente;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteRequestDTO {

    @NotBlank(message = "El rut del cliente es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9kK]$", message = "El rut debe tener formato 12345678-9")
    private String rutCliente;

    @NotBlank(message = "El numero de serie es obligatorio")
    @Size(min = 9, max = 9, message = "El numero de serie debe tener 9 caracteres")
    private String numeroSerie;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String nombreCliente;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede superar los 50 caracteres")
    private String apellidoCliente;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "El telefono debe tener entre 8 y 15 digitos")
    private String telefonoCliente;

    @Email(message = "El email debe tener un formato valido")
    @Size(max = 50, message = "El email no puede superar los 50 caracteres")
    private String emailCliente;

    @NotNull(message = "El id de la profesion es obligatorio")
    private Long idProfesion;

    @Valid
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
