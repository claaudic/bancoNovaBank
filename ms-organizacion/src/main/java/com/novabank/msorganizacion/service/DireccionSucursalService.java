package com.novabank.msorganizacion.service;

import com.novabank.msorganizacion.dto.request.DireccionSucursalRequestDTO;
import com.novabank.msorganizacion.dto.response.DireccionSucursalResponseDTO;
import com.novabank.msorganizacion.exception.BusinessRuleException;
import com.novabank.msorganizacion.exception.ResourceNotFoundException;
import com.novabank.msorganizacion.model.DireccionSucursal;
import com.novabank.msorganizacion.model.Estado;
import com.novabank.msorganizacion.model.Sucursal;
import com.novabank.msorganizacion.model.TipoDireccion;
import com.novabank.msorganizacion.repository.DireccionSucursalRepository;
import com.novabank.msorganizacion.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DireccionSucursalService {

    private final DireccionSucursalRepository direccionRepository;
    private final SucursalRepository sucursalRepository;

    @Transactional
    public DireccionSucursalResponseDTO crear(DireccionSucursalRequestDTO dto) {
        Sucursal sucursal = sucursalRepository.findById(dto.getIdSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no existe"));

        if (sucursal.getEstado() == Estado.INACTIVO) {
            throw new BusinessRuleException("No se puede agregar direccion a una sucursal inactiva");
        }

        DireccionSucursal direccion = dto.toEntity();
        direccion.setSucursal(sucursal);

        DireccionSucursal guardada = direccionRepository.save(direccion);

        log.info("Direccion creada id={} sucursalId={}",
                guardada.getIdDireccion(), sucursal.getIdSucursal());

        return DireccionSucursalResponseDTO.toResponseDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<DireccionSucursalResponseDTO> listarTodas() {
        return direccionRepository.findAll()
                .stream()
                .map(DireccionSucursalResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DireccionSucursalResponseDTO obtenerPorId(Long idDireccion) {
        return DireccionSucursalResponseDTO.toResponseDTO(buscarDireccion(idDireccion));
    }

    @Transactional(readOnly = true)
    public List<DireccionSucursalResponseDTO> listarPorSucursal(Long idSucursal) {
        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal no existe");
        }
        log.debug("Listando direcciones de sucursal id={}", idSucursal);
        return direccionRepository.findBySucursalIdSucursal(idSucursal)
                .stream()
                .map(DireccionSucursalResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DireccionSucursalResponseDTO> listarPorCiudad(String ciudad) {
        return direccionRepository.findByCiudadIgnoreCase(ciudad)
                .stream()
                .map(DireccionSucursalResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DireccionSucursalResponseDTO> listarPorTipo(TipoDireccion tipo) {
        return direccionRepository.findByTipoDireccion(tipo)
                .stream()
                .map(DireccionSucursalResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional
    public DireccionSucursalResponseDTO actualizar(Long idDireccion, DireccionSucursalRequestDTO dto) {
        DireccionSucursal direccion = buscarDireccion(idDireccion);

        Sucursal sucursal = sucursalRepository.findById(dto.getIdSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no existe"));

        if (sucursal.getEstado() == Estado.INACTIVO) {
            throw new BusinessRuleException("No se puede asignar direccion a una sucursal inactiva");
        }

        direccion.setTipoDireccion(dto.getTipoDireccion());
        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());
        direccion.setDepto(dto.getDepto());
        direccion.setCiudad(dto.getCiudad());
        direccion.setRegion(dto.getRegion());
        direccion.setReferencia(dto.getReferencia());
        direccion.setSucursal(sucursal);

        DireccionSucursal actualizada = direccionRepository.save(direccion);

        log.info("Direccion actualizada id={}", idDireccion);

        return DireccionSucursalResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public void eliminar(Long idDireccion) {
        if (!direccionRepository.existsById(idDireccion)) {
            throw new ResourceNotFoundException("Direccion no encontrada");
        }

        log.info("Eliminando direccion id={}", idDireccion);

        direccionRepository.deleteById(idDireccion);
    }

    private DireccionSucursal buscarDireccion(Long idDireccion) {
        return direccionRepository.findById(idDireccion)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion no encontrada"));
    }
}
