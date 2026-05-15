package com.novabank.msorganizacion.service;

import com.novabank.msorganizacion.dto.request.SucursalRequestDTO;
import com.novabank.msorganizacion.dto.response.SucursalResponseDTO;
import com.novabank.msorganizacion.exception.BusinessRuleException;
import com.novabank.msorganizacion.exception.DuplicateResourceException;
import com.novabank.msorganizacion.exception.ResourceNotFoundException;
import com.novabank.msorganizacion.model.Estado;
import com.novabank.msorganizacion.model.Sucursal;
import com.novabank.msorganizacion.repository.DireccionSucursalRepository;
import com.novabank.msorganizacion.repository.EjecutivoRepository;
import com.novabank.msorganizacion.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final EjecutivoRepository ejecutivoRepository;
    private final DireccionSucursalRepository direccionRepository;

    @Transactional
    public SucursalResponseDTO crear(SucursalRequestDTO dto) {
        if (sucursalRepository.existsByNombre(dto.getNombre())) {
            throw new DuplicateResourceException("El nombre de la sucursal ya esta registrado");
        }
        if (sucursalRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("El email de la sucursal ya esta registrado");
        }

        Sucursal sucursal = dto.toEntity();
        sucursal.setEstado(Estado.ACTIVO);
        sucursal.setFechaCreacion(LocalDateTime.now());

        Sucursal guardada = sucursalRepository.save(sucursal);

        log.info("Sucursal creada id={} nombre={}", guardada.getIdSucursal(), guardada.getNombre());

        return SucursalResponseDTO.toResponseDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<SucursalResponseDTO> listarTodas() {
        return sucursalRepository.findAll()
                .stream()
                .map(SucursalResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SucursalResponseDTO obtenerPorId(Long idSucursal) {
        return SucursalResponseDTO.toResponseDTO(buscarSucursal(idSucursal));
    }

    @Transactional(readOnly = true)
    public List<SucursalResponseDTO> listarActivas() {
        return sucursalRepository.findByEstado(Estado.ACTIVO)
                .stream()
                .map(SucursalResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SucursalResponseDTO> buscarPorNombre(String nombre) {
        log.debug("Buscando sucursales con nombre que contiene: {}", nombre);
        return sucursalRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(SucursalResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional
    public SucursalResponseDTO actualizar(Long idSucursal, SucursalRequestDTO dto) {
        Sucursal sucursal = buscarSucursal(idSucursal);

        if (sucursal.getEstado() == Estado.INACTIVO) {
            throw new BusinessRuleException("No se puede actualizar una sucursal inactiva");
        }

        if (!sucursal.getNombre().equals(dto.getNombre())
                && sucursalRepository.existsByNombre(dto.getNombre())) {
            throw new DuplicateResourceException("El nombre de la sucursal ya esta registrado");
        }
        if (!sucursal.getEmail().equals(dto.getEmail())
                && sucursalRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }

        sucursal.setNombre(dto.getNombre());
        sucursal.setEmail(dto.getEmail());
        sucursal.setTelefono(dto.getTelefono());

        Sucursal actualizada = sucursalRepository.save(sucursal);

        log.info("Sucursal actualizada id={}", idSucursal);

        return SucursalResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public SucursalResponseDTO activar(Long idSucursal) {
        Sucursal sucursal = buscarSucursal(idSucursal);

        if (sucursal.getEstado() == Estado.ACTIVO) {
            throw new BusinessRuleException("La sucursal ya esta activa");
        }

        sucursal.setEstado(Estado.ACTIVO);
        Sucursal actualizada = sucursalRepository.save(sucursal);

        log.info("Sucursal activada id={}", idSucursal);

        return SucursalResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public SucursalResponseDTO desactivar(Long idSucursal) {
        Sucursal sucursal = buscarSucursal(idSucursal);

        if (sucursal.getEstado() == Estado.INACTIVO) {
            throw new BusinessRuleException("La sucursal ya esta inactiva");
        }

        sucursal.setEstado(Estado.INACTIVO);
        Sucursal actualizada = sucursalRepository.save(sucursal);

        log.info("Sucursal desactivada id={}", idSucursal);

        return SucursalResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public void eliminar(Long idSucursal) {
        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal no encontrada");
        }

        if (ejecutivoRepository.existsBySucursalIdSucursal(idSucursal)) {
            throw new BusinessRuleException(
                    "No se puede eliminar la sucursal porque tiene ejecutivos asociados");
        }
        if (direccionRepository.existsBySucursalIdSucursal(idSucursal)) {
            throw new BusinessRuleException(
                    "No se puede eliminar la sucursal porque tiene direcciones asociadas");
        }

        log.info("Eliminando sucursal id={}", idSucursal);

        sucursalRepository.deleteById(idSucursal);
    }

    private Sucursal buscarSucursal(Long idSucursal) {
        return sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));
    }
}
