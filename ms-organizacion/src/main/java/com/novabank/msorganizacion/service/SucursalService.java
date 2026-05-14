package com.novabank.msorganizacion.service;

import com.novabank.msorganizacion.dto.SucursalRequestDTO;
import com.novabank.msorganizacion.dto.SucursalResponseDTO;
import com.novabank.msorganizacion.model.Sucursal;
import com.novabank.msorganizacion.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalService {
    private final SucursalRepository sucursalRepository;

    public List<SucursalResponseDTO> listarTodas() {
        return sucursalRepository.findAll().stream()
                .map(SucursalResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public SucursalResponseDTO obtenerPorId(Long id) {
        Sucursal s = sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
        return SucursalResponseDTO.fromEntity(s);
    }

    public SucursalResponseDTO crear(SucursalRequestDTO dto) {
        Sucursal guardada = sucursalRepository.save(dto.toEntity());
        return SucursalResponseDTO.fromEntity(guardada);
    }

    public void eliminar(Long id) {
        sucursalRepository.deleteById(id);
    }
}
