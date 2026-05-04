package com.novabank.msclientes.dto;

import jakarta.validation.constraints.NotBlank;

public class ProfesionRequestDTO {

    @NotBlank
    private String nombreProfesion;

}
