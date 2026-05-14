package com.novabank.mscuentas.service;

import com.novabank.mscuentas.dto.request.TipoCuentaRequestDTO;
import com.novabank.mscuentas.dto.response.TipoCuentaResponseDTO;
import com.novabank.mscuentas.exception.BusinessRuleException;
import com.novabank.mscuentas.exception.DuplicateResourceException;
import com.novabank.mscuentas.exception.ResourceNotFoundException;
import com.novabank.mscuentas.model.TipoCuenta;
import com.novabank.mscuentas.repository.CuentaRepository;
import com.novabank.mscuentas.repository.TipoCuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TipoCuentaService {

    private final TipoCuentaRepository tipoCuentaRepository;
    private final CuentaRepository cuentaRepository;

    @Transactional
    public TipoCuentaResponseDTO crearTipoCuenta(TipoCuentaRequestDTO dto) {
        if (tipoCuentaRepository.existsByNombreTipoCuenta(dto.getNombreTipoCuenta())) {
            throw new DuplicateResourceException("El tipo de cuenta ya existe");
        }

        TipoCuenta guardado = tipoCuentaRepository.save(dto.toEntity());

        log.info("Tipo de cuenta creado id={} nombre={}",
                guardado.getIdTipoCuenta(), guardado.getNombreTipoCuenta());

        return TipoCuentaResponseDTO.toResponseDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<TipoCuentaResponseDTO> obtenerTiposCuenta() {
        return tipoCuentaRepository.findAll()
                .stream()
                .map(TipoCuentaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TipoCuentaResponseDTO obtenerPorId(Long idTipoCuenta) {
        return TipoCuentaResponseDTO.toResponseDTO(buscarTipoCuenta(idTipoCuenta));
    }

    @Transactional(readOnly = true)
    public List<TipoCuentaResponseDTO> buscarPorNombre(String nombre) {
        log.debug("Buscando tipos de cuenta con nombre que contiene: {}", nombre);
        return tipoCuentaRepository.findByNombreTipoCuentaContainingIgnoreCase(nombre)
                .stream()
                .map(TipoCuentaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional
    public TipoCuentaResponseDTO actualizarTipoCuenta(Long idTipoCuenta, TipoCuentaRequestDTO dto) {
        TipoCuenta tipoCuenta = buscarTipoCuenta(idTipoCuenta);

        if (!tipoCuenta.getNombreTipoCuenta().equals(dto.getNombreTipoCuenta())
                && tipoCuentaRepository.existsByNombreTipoCuenta(dto.getNombreTipoCuenta())) {
            throw new DuplicateResourceException("El tipo de cuenta ya existe");
        }

        tipoCuenta.setNombreTipoCuenta(dto.getNombreTipoCuenta());

        TipoCuenta actualizado = tipoCuentaRepository.save(tipoCuenta);

        log.info("Tipo de cuenta actualizado id={}", idTipoCuenta);

        return TipoCuentaResponseDTO.toResponseDTO(actualizado);
    }

    @Transactional
    public void eliminarTipoCuenta(Long idTipoCuenta) {
        if (!tipoCuentaRepository.existsById(idTipoCuenta)) {
            throw new ResourceNotFoundException("Tipo de cuenta no encontrado");
        }

        if (cuentaRepository.existsByTipoCuentaIdTipoCuenta(idTipoCuenta)) {
            throw new BusinessRuleException(
                    "No se puede eliminar el tipo de cuenta porque tiene cuentas asociadas");
        }

        log.info("Eliminando tipo de cuenta id={}", idTipoCuenta);

        tipoCuentaRepository.deleteById(idTipoCuenta);
    }

    private TipoCuenta buscarTipoCuenta(Long idTipoCuenta) {
        return tipoCuentaRepository.findById(idTipoCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de cuenta no encontrado"));
    }
}
