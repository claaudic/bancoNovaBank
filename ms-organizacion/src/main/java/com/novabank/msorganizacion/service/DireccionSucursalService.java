package com.novabank.msorganizacion.service;

import com.novabank.msorganizacion.dto.DireccionSucursalRequestDTO;
import com.novabank.msorganizacion.dto.DireccionSucursalResponseDTO;
import com.novabank.msorganizacion.model.DireccionSucursal;
import com.novabank.msorganizacion.model.Sucursal;
import com.novabank.msorganizacion.repository.DireccionSucursalRepository;
import com.novabank.msorganizacion.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DireccionSucursalService {

    private final DireccionSucursalRepository direccionRepository;
    private final SucursalRepository sucursalRepository;

    public DireccionSucursalResponseDTO crear(DireccionSucursalRequestDTO dto) {
        Sucursal sucursal = sucursalRepository.findById(dto.getIdSucursal())
                .orElseThrow(() -> new RuntimeException("Error: La sucursal no existe."));

        DireccionSucursal nueva = new DireccionSucursal();
        nueva.setTipoDireccion(dto.getTipoDireccion());
        nueva.setCalle(dto.getCalle());
        nueva.setNumero(dto.getNumero());
        nueva.setDepto(dto.getDepto());
        nueva.setCiudad(dto.getCiudad());
        nueva.setRegion(dto.getRegion());
        nueva.setSucursal(sucursal);

        DireccionSucursal guardada = direccionRepository.save(nueva);
        return DireccionSucursalResponseDTO.fromEntity(guardada);
    }

    public DireccionSucursalResponseDTO actualizar(Long id, DireccionSucursalRequestDTO dto) {
        DireccionSucursal existente = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: La dirección no existe."));

        Sucursal sucursal = sucursalRepository.findById(dto.getIdSucursal())
                .orElseThrow(() -> new RuntimeException("Error: La sucursal no existe."));

        existente.setTipoDireccion(dto.getTipoDireccion());
        existente.setCalle(dto.getCalle());
        existente.setNumero(dto.getNumero());
        existente.setDepto(dto.getDepto());
        existente.setCiudad(dto.getCiudad());
        existente.setRegion(dto.getRegion());
        existente.setSucursal(sucursal);

        DireccionSucursal actualizada = direccionRepository.save(existente);
        return DireccionSucursalResponseDTO.fromEntity(actualizada);
    }

    public void eliminar(Long id) {
        direccionRepository.deleteById(id);
    }

    public List<DireccionSucursalResponseDTO> listarPorSucursal(Long idSucursal) {
        return direccionRepository.findBySucursal_IdSucursal(idSucursal).stream()
                .map(DireccionSucursalResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}