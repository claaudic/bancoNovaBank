package com.novabank.msorganizacion.service;

import com.novabank.msorganizacion.dto.request.EjecutivoRequestDTO;
import com.novabank.msorganizacion.dto.response.EjecutivoResponseDTO;
import com.novabank.msorganizacion.exception.BusinessRuleException;
import com.novabank.msorganizacion.exception.DuplicateResourceException;
import com.novabank.msorganizacion.exception.ResourceNotFoundException;
import com.novabank.msorganizacion.model.Ejecutivo;
import com.novabank.msorganizacion.model.Estado;
import com.novabank.msorganizacion.model.Sucursal;
import com.novabank.msorganizacion.repository.EjecutivoRepository;
import com.novabank.msorganizacion.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EjecutivoService {

    private final EjecutivoRepository ejecutivoRepository;
    private final SucursalRepository sucursalRepository;

    @Transactional
    public EjecutivoResponseDTO crear(EjecutivoRequestDTO dto) {
        if (ejecutivoRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("El email del ejecutivo ya esta registrado");
        }

        Sucursal sucursal = sucursalRepository.findById(dto.getIdSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no existe"));

        if (sucursal.getEstado() == Estado.INACTIVO) {
            throw new BusinessRuleException("No se puede asignar ejecutivo a una sucursal inactiva");
        }

        Ejecutivo ejecutivo = dto.toEntity();
        ejecutivo.setSucursal(sucursal);
        ejecutivo.setEstado(Estado.ACTIVO);
        ejecutivo.setFechaIngreso(LocalDate.now());

        Ejecutivo guardado = ejecutivoRepository.save(ejecutivo);

        log.info("Ejecutivo creado id={} email={} sucursalId={}",
                guardado.getIdEjecutivo(), guardado.getEmail(), sucursal.getIdSucursal());

        return EjecutivoResponseDTO.toResponseDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<EjecutivoResponseDTO> listarTodos() {
        return ejecutivoRepository.findAll()
                .stream()
                .map(EjecutivoResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EjecutivoResponseDTO obtenerPorId(Long idEjecutivo) {
        return EjecutivoResponseDTO.toResponseDTO(buscarEjecutivo(idEjecutivo));
    }

    @Transactional(readOnly = true)
    public List<EjecutivoResponseDTO> listarPorSucursal(Long idSucursal) {
        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal no existe");
        }
        log.debug("Listando ejecutivos de sucursal id={}", idSucursal);
        return ejecutivoRepository.findBySucursalIdSucursal(idSucursal)
                .stream()
                .map(EjecutivoResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EjecutivoResponseDTO> listarActivosPorSucursal(Long idSucursal) {
        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal no existe");
        }
        return ejecutivoRepository.findBySucursalIdSucursalAndEstado(idSucursal, Estado.ACTIVO)
                .stream()
                .map(EjecutivoResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional
    public EjecutivoResponseDTO actualizar(Long idEjecutivo, EjecutivoRequestDTO dto) {
        Ejecutivo ejecutivo = buscarEjecutivo(idEjecutivo);

        if (ejecutivo.getEstado() == Estado.INACTIVO) {
            throw new BusinessRuleException("No se puede actualizar un ejecutivo inactivo");
        }

        if (!ejecutivo.getEmail().equals(dto.getEmail())
                && ejecutivoRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }

        Sucursal sucursal = sucursalRepository.findById(dto.getIdSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no existe"));

        ejecutivo.setNombre(dto.getNombre());
        ejecutivo.setApellido(dto.getApellido());
        ejecutivo.setEmail(dto.getEmail());
        ejecutivo.setTelefono(dto.getTelefono());
        ejecutivo.setCargo(dto.getCargo());
        ejecutivo.setSucursal(sucursal);

        Ejecutivo actualizado = ejecutivoRepository.save(ejecutivo);

        log.info("Ejecutivo actualizado id={}", idEjecutivo);

        return EjecutivoResponseDTO.toResponseDTO(actualizado);
    }

    @Transactional
    public EjecutivoResponseDTO activar(Long idEjecutivo) {
        Ejecutivo ejecutivo = buscarEjecutivo(idEjecutivo);

        if (ejecutivo.getEstado() == Estado.ACTIVO) {
            throw new BusinessRuleException("El ejecutivo ya esta activo");
        }

        ejecutivo.setEstado(Estado.ACTIVO);
        Ejecutivo actualizado = ejecutivoRepository.save(ejecutivo);

        log.info("Ejecutivo activado id={}", idEjecutivo);

        return EjecutivoResponseDTO.toResponseDTO(actualizado);
    }

    @Transactional
    public EjecutivoResponseDTO desactivar(Long idEjecutivo) {
        Ejecutivo ejecutivo = buscarEjecutivo(idEjecutivo);

        if (ejecutivo.getEstado() == Estado.INACTIVO) {
            throw new BusinessRuleException("El ejecutivo ya esta inactivo");
        }

        ejecutivo.setEstado(Estado.INACTIVO);
        Ejecutivo actualizado = ejecutivoRepository.save(ejecutivo);

        log.info("Ejecutivo desactivado id={}", idEjecutivo);

        return EjecutivoResponseDTO.toResponseDTO(actualizado);
    }

    @Transactional
    public void eliminar(Long idEjecutivo) {
        if (!ejecutivoRepository.existsById(idEjecutivo)) {
            throw new ResourceNotFoundException("Ejecutivo no encontrado");
        }

        log.info("Eliminando ejecutivo id={}", idEjecutivo);

        ejecutivoRepository.deleteById(idEjecutivo);
    }

    private Ejecutivo buscarEjecutivo(Long idEjecutivo) {
        return ejecutivoRepository.findById(idEjecutivo)
                .orElseThrow(() -> new ResourceNotFoundException("Ejecutivo no encontrado"));
    }
}
