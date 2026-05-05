package com.novabank.msclientes.dto;

import com.novabank.msclientes.model.Profesion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfesionResponseDTO {

    private Long idProfesion;
    private String nombreProfesion;

    public static ProfesionResponseDTO toProfesionResponseDTO(Profesion profesionRequestDTO) {
        ProfesionResponseDTO responseDTO = new ProfesionResponseDTO (
                profesionRequestDTO.getIdProfesion(),
                profesionRequestDTO.getNombreProfesion()
        );
        return responseDTO;
    }
}
