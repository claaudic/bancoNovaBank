package com.novabank.msclientes.dto.response;

import com.novabank.msclientes.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Long idUsuario;
    private String username;
    private String rol;
    private LocalDateTime fechaCreacion;

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getRol(),
                usuario.getFechaCreacion()
        );
    }
}
