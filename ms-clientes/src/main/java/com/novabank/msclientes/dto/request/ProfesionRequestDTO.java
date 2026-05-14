package com.novabank.msclientes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfesionRequestDTO {

    @NotBlank(message = "El nombre de la profesion es obligatorio")
    @Size(max = 50, message = "El nombre de la profesion no puede superar los 50 caracteres")
    private String nombreProfesion;
}
