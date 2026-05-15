package com.novabank.msorganizacion.dto.request;

import com.novabank.msorganizacion.model.Sucursal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SucursalRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    @Size(max = 80, message = "El email no puede superar los 80 caracteres")
    private String email;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "El telefono debe tener entre 8 y 15 digitos")
    private String telefono;

    public Sucursal toEntity() {
        Sucursal s = new Sucursal();
        s.setNombre(nombre);
        s.setEmail(email);
        s.setTelefono(telefono);
        return s;
    }
}
