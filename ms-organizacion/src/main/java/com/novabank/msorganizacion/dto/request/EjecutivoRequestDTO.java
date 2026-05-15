package com.novabank.msorganizacion.dto.request;

import com.novabank.msorganizacion.model.Ejecutivo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EjecutivoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede superar los 50 caracteres")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    @Size(max = 80, message = "El email no puede superar los 80 caracteres")
    private String email;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "El telefono debe tener entre 8 y 15 digitos")
    private String telefono;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 40, message = "El cargo no puede superar los 40 caracteres")
    private String cargo;

    @NotNull(message = "El id de la sucursal es obligatorio")
    private Long idSucursal;

    public Ejecutivo toEntity() {
        Ejecutivo e = new Ejecutivo();
        e.setNombre(nombre);
        e.setApellido(apellido);
        e.setEmail(email);
        e.setTelefono(telefono);
        e.setCargo(cargo);
        return e;
    }
}
