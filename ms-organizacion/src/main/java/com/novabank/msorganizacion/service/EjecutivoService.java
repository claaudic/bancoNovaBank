package com.novabank.msorganizacion.service;

import com.novabank.msorganizacion.dto.EjecutivoRequestDTO;
import com.novabank.msorganizacion.dto.EjecutivoResponseDTO;
import com.novabank.msorganizacion.model.Ejecutivo;
import com.novabank.msorganizacion.model.Sucursal;
import com.novabank.msorganizacion.repository.EjecutivoRepository;
import com.novabank.msorganizacion.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EjecutivoService {

    private final EjecutivoRepository ejecutivoRepository;
    private final SucursalRepository sucursalRepository;

    public EjecutivoResponseDTO crear(EjecutivoRequestDTO dto) {
        Sucursal sucursal = sucursalRepository.findById(dto.getIdSucursal())
                .orElseThrow(() -> new RuntimeException("Error: La sucursal no existe."));

        Ejecutivo nuevo = new Ejecutivo();
        nuevo.setNombre(dto.getNombre());
        nuevo.setApellido(dto.getApellido());
        nuevo.setEmail(dto.getEmail());
        nuevo.setTelefono(dto.getTelefono());
        nuevo.setSucursal(sucursal);

        Ejecutivo guardado = ejecutivoRepository.save(nuevo);
        return EjecutivoResponseDTO.fromEntity(guardado);
    }

    public EjecutivoResponseDTO actualizar(Long id, EjecutivoRequestDTO dto) {
        Ejecutivo existente = ejecutivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: El ejecutivo no existe."));

        Sucursal sucursal = sucursalRepository.findById(dto.getIdSucursal())
                .orElseThrow(() -> new RuntimeException("Error: La sucursal no existe."));

        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setEmail(dto.getEmail());
        existente.setTelefono(dto.getTelefono());
        existente.setSucursal(sucursal);

        Ejecutivo actualizado = ejecutivoRepository.save(existente);
        return EjecutivoResponseDTO.fromEntity(actualizado);
    }

    public void eliminar(Long id) {
        ejecutivoRepository.deleteById(id);
    }

    public List<EjecutivoResponseDTO> listarPorSucursal(Long idSucursal) {
        return ejecutivoRepository.findBySucursal_IdSucursal(idSucursal).stream()
                .map(EjecutivoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}