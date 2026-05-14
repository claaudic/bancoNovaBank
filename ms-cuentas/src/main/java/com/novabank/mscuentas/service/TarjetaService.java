package com.novabank.mscuentas.service;

import com.novabank.mscuentas.dto.request.TarjetaRequestDTO;
import com.novabank.mscuentas.dto.response.TarjetaResponseDTO;
import com.novabank.mscuentas.exception.BusinessRuleException;
import com.novabank.mscuentas.exception.DuplicateResourceException;
import com.novabank.mscuentas.exception.ResourceNotFoundException;
import com.novabank.mscuentas.model.Cuenta;
import com.novabank.mscuentas.model.EstadoCuenta;
import com.novabank.mscuentas.model.EstadoTarjeta;
import com.novabank.mscuentas.model.Tarjeta;
import com.novabank.mscuentas.repository.CuentaRepository;
import com.novabank.mscuentas.repository.TarjetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TarjetaService {

    private final TarjetaRepository tarjetaRepository;
    private final CuentaRepository cuentaRepository;

    @Transactional
    public TarjetaResponseDTO crearTarjeta(TarjetaRequestDTO dto) {
        if (tarjetaRepository.existsByNumeroTarjeta(dto.getNumeroTarjeta())) {
            throw new DuplicateResourceException("El numero de tarjeta ya existe");
        }

        Cuenta cuenta = cuentaRepository.findById(dto.getIdCuenta())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) {
            throw new BusinessRuleException(
                    "No se puede crear tarjeta sobre una cuenta no activa (estado=" + cuenta.getEstado() + ")");
        }

        Tarjeta tarjeta = dto.toEntity();
        tarjeta.setEstado(EstadoTarjeta.ACTIVA);
        tarjeta.setCuenta(cuenta);

        Tarjeta guardada = tarjetaRepository.save(tarjeta);

        log.info("Tarjeta creada id={} numero={} cuentaId={}",
                guardada.getIdTarjeta(), guardada.getNumeroTarjeta(), cuenta.getIdCuenta());

        return TarjetaResponseDTO.toResponseDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<TarjetaResponseDTO> obtenerTarjetas() {
        return tarjetaRepository.findAll()
                .stream()
                .map(TarjetaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TarjetaResponseDTO obtenerTarjetaPorId(Long idTarjeta) {
        return TarjetaResponseDTO.toResponseDTO(buscarTarjeta(idTarjeta));
    }

    @Transactional(readOnly = true)
    public List<TarjetaResponseDTO> obtenerTarjetasPorIdCuenta(Long idCuenta) {
        log.debug("Buscando tarjetas para cuenta id={}", idCuenta);
        return tarjetaRepository.findByCuentaIdCuenta(idCuenta)
                .stream()
                .map(TarjetaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TarjetaResponseDTO> obtenerTarjetasPorRutCliente(String rutCliente) {
        return tarjetaRepository.findByCuentaRutCliente(rutCliente)
                .stream()
                .map(TarjetaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TarjetaResponseDTO> obtenerTarjetasPorEstado(EstadoTarjeta estado) {
        return tarjetaRepository.findByEstado(estado)
                .stream()
                .map(TarjetaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TarjetaResponseDTO> obtenerTarjetasVencidas() {
        return tarjetaRepository.findByFechaVencimientoBefore(LocalDate.now())
                .stream()
                .map(TarjetaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional
    public TarjetaResponseDTO actualizarTarjeta(Long idTarjeta, TarjetaRequestDTO dto) {
        Tarjeta tarjeta = buscarTarjeta(idTarjeta);

        if (tarjeta.getEstado() == EstadoTarjeta.INACTIVA) {
            throw new BusinessRuleException("No se puede actualizar una tarjeta inactiva");
        }

        if (!tarjeta.getNumeroTarjeta().equals(dto.getNumeroTarjeta())
                && tarjetaRepository.existsByNumeroTarjeta(dto.getNumeroTarjeta())) {
            throw new DuplicateResourceException("El numero de tarjeta ya existe");
        }

        Cuenta cuenta = cuentaRepository.findById(dto.getIdCuenta())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        tarjeta.setNumeroTarjeta(dto.getNumeroTarjeta());
        tarjeta.setFechaVencimiento(dto.getFechaVencimiento());
        tarjeta.setCvv(dto.getCvv());
        tarjeta.setCuenta(cuenta);

        Tarjeta actualizada = tarjetaRepository.save(tarjeta);

        log.info("Tarjeta actualizada id={}", idTarjeta);

        return TarjetaResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public void eliminarTarjeta(Long idTarjeta) {
        if (!tarjetaRepository.existsById(idTarjeta)) {
            throw new ResourceNotFoundException("Tarjeta no encontrada");
        }

        log.info("Eliminando tarjeta id={}", idTarjeta);

        tarjetaRepository.deleteById(idTarjeta);
    }

    @Transactional
    public TarjetaResponseDTO bloquearTarjeta(Long idTarjeta) {
        Tarjeta tarjeta = buscarTarjeta(idTarjeta);

        if (tarjeta.getEstado() == EstadoTarjeta.BLOQUEADA) {
            throw new BusinessRuleException("La tarjeta ya esta bloqueada");
        }
        if (tarjeta.getEstado() == EstadoTarjeta.INACTIVA) {
            throw new BusinessRuleException("No se puede bloquear una tarjeta inactiva");
        }
        if (tarjeta.getEstado() == EstadoTarjeta.VENCIDA) {
            throw new BusinessRuleException("No se puede bloquear una tarjeta vencida");
        }

        tarjeta.setEstado(EstadoTarjeta.BLOQUEADA);
        Tarjeta actualizada = tarjetaRepository.save(tarjeta);

        log.info("Tarjeta bloqueada id={}", idTarjeta);

        return TarjetaResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public TarjetaResponseDTO activarTarjeta(Long idTarjeta) {
        Tarjeta tarjeta = buscarTarjeta(idTarjeta);

        if (tarjeta.getEstado() == EstadoTarjeta.ACTIVA) {
            throw new BusinessRuleException("La tarjeta ya esta activa");
        }
        if (tarjeta.getEstado() == EstadoTarjeta.INACTIVA) {
            throw new BusinessRuleException("No se puede reactivar una tarjeta inactiva");
        }
        if (tarjeta.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("No se puede activar una tarjeta vencida");
        }

        tarjeta.setEstado(EstadoTarjeta.ACTIVA);
        Tarjeta actualizada = tarjetaRepository.save(tarjeta);

        log.info("Tarjeta activada id={}", idTarjeta);

        return TarjetaResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public int marcarTarjetasVencidas() {
        List<Tarjeta> vencidas = tarjetaRepository.findByFechaVencimientoBefore(LocalDate.now())
                .stream()
                .filter(t -> t.getEstado() != EstadoTarjeta.VENCIDA && t.getEstado() != EstadoTarjeta.INACTIVA)
                .toList();

        vencidas.forEach(t -> t.setEstado(EstadoTarjeta.VENCIDA));
        tarjetaRepository.saveAll(vencidas);

        log.info("Marcadas {} tarjetas como VENCIDAS", vencidas.size());

        return vencidas.size();
    }

    private Tarjeta buscarTarjeta(Long idTarjeta) {
        return tarjetaRepository.findById(idTarjeta)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada"));
    }
}
