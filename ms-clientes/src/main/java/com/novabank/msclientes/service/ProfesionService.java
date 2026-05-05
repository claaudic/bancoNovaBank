package com.novabank.msclientes.service;

import com.novabank.msclientes.dto.ProfesionRequestDTO;
import com.novabank.msclientes.dto.ProfesionResponseDTO;
import com.novabank.msclientes.model.Profesion;
import com.novabank.msclientes.repository.ProfesionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfesionService {

    private final ProfesionRepository profesionRepository;

    public ProfesionResponseDTO crearProfesion(ProfesionRequestDTO profesionRequestDTO) {
        Profesion profesion = new Profesion();
        profesion.setNombreProfesion(profesionRequestDTO.getNombreProfesion());

        Profesion guardada = profesionRepository.save(profesion);

        return ProfesionResponseDTO.toProfesionResponseDTO(guardada);
    }

    public List<ProfesionResponseDTO> obtenerProfesiones() {
        return profesionRepository.findAll()
                .stream()
                .map(ProfesionResponseDTO::toProfesionResponseDTO)
                .toList();
    }

    public ProfesionResponseDTO obtenerPorId(Long id) {

        Profesion profesion = profesionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesion no encontrada"));

        return ProfesionResponseDTO.toProfesionResponseDTO(profesion);
    }

    public ProfesionResponseDTO actualizarProfesion(Long id, ProfesionRequestDTO dto) {

        Profesion profesion = profesionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesion no encontrada"));

        profesion.setNombreProfesion(dto.getNombreProfesion());

        Profesion actualizada = profesionRepository.save(profesion);

        return ProfesionResponseDTO.toProfesionResponseDTO(actualizada);
    }

    public void eliminarProfesion(Long id) {

        if (!profesionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesion no encontrada");
        }

        profesionRepository.deleteById(id);
    }
}