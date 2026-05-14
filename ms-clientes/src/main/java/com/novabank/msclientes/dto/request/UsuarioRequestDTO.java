package com.novabank.msclientes.dto.request;

import com.novabank.msclientes.model.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max = 50, message = "El username debe tener entre 4 y 50 caracteres")
    private String username;

    @NotBlank(message = "La password es obligatoria")
    @Size(min = 6, max = 100, message = "La password debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "^(ADMIN|USER|OPERADOR)$", message = "El rol debe ser ADMIN, USER o OPERADOR")
    private String rol;

    public Usuario toEntity() {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setRol(rol);
        return usuario;
    }
}
