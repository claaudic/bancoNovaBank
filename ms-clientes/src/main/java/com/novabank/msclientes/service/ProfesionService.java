package com.novabank.msclientes.service;

import com.novabank.msclientes.dto.request.ProfesionRequestDTO;
import com.novabank.msclientes.dto.response.ProfesionResponseDTO;
import com.novabank.msclientes.exception.BusinessRuleException;
import com.novabank.msclientes.exception.DuplicateResourceException;
import com.novabank.msclientes.exception.ResourceNotFoundException;
import com.novabank.msclientes.model.Profesion;
import com.novabank.msclientes.repository.ClienteRepository;
import com.novabank.msclientes.repository.ProfesionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfesionService {

    private final ProfesionRepository profesionRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public ProfesionResponseDTO crearProfesion(ProfesionRequestDTO profesionRequestDTO) {
        if (profesionRepository.existsByNombreProfesion(profesionRequestDTO.getNombreProfesion())) {
            throw new DuplicateResourceException("La profesion ya existe");
        }

        Profesion profesion = new Profesion();
        profesion.setNombreProfesion(profesionRequestDTO.getNombreProfesion());

        Profesion guardada = profesionRepository.save(profesion);

        log.info("Profesion creada id={} nombre={}", guardada.getIdProfesion(), guardada.getNombreProfesion());

        return ProfesionResponseDTO.toProfesionResponseDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<ProfesionResponseDTO> obtenerProfesiones() {
        return profesionRepository.findAll()
                .stream()
                .map(ProfesionResponseDTO::toProfesionResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProfesionResponseDTO obtenerPorId(Long id) {
        return ProfesionResponseDTO.toProfesionResponseDTO(buscarProfesion(id));
    }

    @Transactional(readOnly = true)
    public List<ProfesionResponseDTO> buscarPorNombre(String nombre) {
        log.debug("Buscando profesiones que contienen: {}", nombre);
        return profesionRepository.findByNombreProfesionContainingIgnoreCase(nombre)
                .stream()
                .map(ProfesionResponseDTO::toProfesionResponseDTO)
                .toList();
    }

    @Transactional
    public ProfesionResponseDTO actualizarProfesion(Long id, ProfesionRequestDTO dto) {
        Profesion profesion = buscarProfesion(id);

        if (!profesion.getNombreProfesion().equals(dto.getNombreProfesion())
                && profesionRepository.existsByNombreProfesion(dto.getNombreProfesion())) {
            throw new DuplicateResourceException("La profesion ya existe");
        }

        profesion.setNombreProfesion(dto.getNombreProfesion());

        Profesion actualizada = profesionRepository.save(profesion);

        log.info("Profesion actualizada id={}", id);

        return ProfesionResponseDTO.toProfesionResponseDTO(actualizada);
    }

    @Transactional
    public void eliminarProfesion(Long id) {
        if (!profesionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Profesion no encontrada");
        }

        if (!clienteRepository.findByProfesionIdProfesion(id).isEmpty()) {
            throw new BusinessRuleException("No se puede eliminar la profesion porque tiene clientes asociados");
        }

        log.info("Eliminando profesion id={}", id);

        profesionRepository.deleteById(id);
    }

    private Profesion buscarProfesion(Long id) {
        return profesionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesion no encontrada"));
    }
}
