package com.novabank.mscuentas.service;

import com.novabank.mscuentas.client.ClienteFeignClient;
import com.novabank.mscuentas.dto.request.CuentaRequestDTO;
import com.novabank.mscuentas.dto.request.MontoRequestDTO;
import com.novabank.mscuentas.dto.request.TransferenciaRequestDTO;
import com.novabank.mscuentas.dto.response.CuentaResponseDTO;
import com.novabank.mscuentas.dto.response.TransferenciaResponseDTO;
import com.novabank.mscuentas.exception.BusinessRuleException;
import com.novabank.mscuentas.exception.DuplicateResourceException;
import com.novabank.mscuentas.exception.RemoteServiceException;
import com.novabank.mscuentas.exception.ResourceNotFoundException;
import com.novabank.mscuentas.model.Cuenta;
import com.novabank.mscuentas.model.EstadoCuenta;
import com.novabank.mscuentas.model.TipoCuenta;
import com.novabank.mscuentas.repository.CuentaRepository;
import com.novabank.mscuentas.repository.TipoCuentaRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final TipoCuentaRepository tipoCuentaRepository;
    private final ClienteFeignClient clienteFeignClient;

    @Transactional
    public CuentaResponseDTO crearCuenta(CuentaRequestDTO dto) {
        if (cuentaRepository.existsByNumeroCuenta(dto.getNumeroCuenta())) {
            throw new DuplicateResourceException("El numero de cuenta ya existe");
        }

        TipoCuenta tipoCuenta = tipoCuentaRepository.findById(dto.getIdTipoCuenta())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de cuenta no existe"));

        validarClienteExiste(dto.getRutCliente());

        Cuenta cuenta = dto.toEntity();
        cuenta.setFechaCreacion(LocalDate.now());
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setTipoCuenta(tipoCuenta);

        Cuenta guardada = cuentaRepository.save(cuenta);

        log.info("Cuenta creada id={} numero={} rutCliente={}",
                guardada.getIdCuenta(), guardada.getNumeroCuenta(), guardada.getRutCliente());

        return CuentaResponseDTO.toResponseDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<CuentaResponseDTO> obtenerCuentas() {
        return cuentaRepository.findAll()
                .stream()
                .map(CuentaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CuentaResponseDTO obtenerCuentaPorId(Long idCuenta) {
        return CuentaResponseDTO.toResponseDTO(buscarCuenta(idCuenta));
    }

    @Transactional(readOnly = true)
    public List<CuentaResponseDTO> obtenerCuentasPorRutCliente(String rutCliente) {
        log.debug("Buscando cuentas para rutCliente={}", rutCliente);
        return cuentaRepository.findByRutCliente(rutCliente)
                .stream()
                .map(CuentaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CuentaResponseDTO> obtenerActivasPorRutCliente(String rutCliente) {
        return cuentaRepository.findByRutClienteAndEstado(rutCliente, EstadoCuenta.ACTIVA)
                .stream()
                .map(CuentaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CuentaResponseDTO> obtenerCuentasPorEstado(EstadoCuenta estado) {
        return cuentaRepository.findByEstado(estado)
                .stream()
                .map(CuentaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CuentaResponseDTO> obtenerCuentasPorRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new BusinessRuleException("Las fechas desde y hasta son obligatorias");
        }
        if (desde.isAfter(hasta)) {
            throw new BusinessRuleException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        return cuentaRepository.findByFechaCreacionBetween(desde, hasta)
                .stream()
                .map(CuentaResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal saldoTotalActivoPorCliente(String rutCliente) {
        validarClienteExiste(rutCliente);
        return cuentaRepository.saldoTotalActivoPorRutCliente(rutCliente);
    }

    @Transactional
    public CuentaResponseDTO actualizarCuenta(Long idCuenta, CuentaRequestDTO dto) {
        Cuenta cuenta = buscarCuenta(idCuenta);

        if (cuenta.getEstado() == EstadoCuenta.INACTIVA) {
            throw new BusinessRuleException("No se puede actualizar una cuenta inactiva");
        }

        if (!cuenta.getNumeroCuenta().equals(dto.getNumeroCuenta())
                && cuentaRepository.existsByNumeroCuenta(dto.getNumeroCuenta())) {
            throw new DuplicateResourceException("El numero de cuenta ya existe");
        }

        TipoCuenta tipoCuenta = tipoCuentaRepository.findById(dto.getIdTipoCuenta())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de cuenta no existe"));

        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setSaldo(dto.getSaldo());
        cuenta.setRutCliente(dto.getRutCliente());
        cuenta.setTipoCuenta(tipoCuenta);

        Cuenta actualizada = cuentaRepository.save(cuenta);

        log.info("Cuenta actualizada id={}", idCuenta);

        return CuentaResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public void eliminarCuenta(Long idCuenta) {
        Cuenta cuenta = buscarCuenta(idCuenta);

        if (cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuleException("No se puede eliminar una cuenta con saldo positivo");
        }

        log.info("Eliminando cuenta id={}", idCuenta);

        cuentaRepository.delete(cuenta);
    }

    @Transactional
    public CuentaResponseDTO depositar(Long idCuenta, MontoRequestDTO dto) {
        Cuenta cuenta = buscarCuenta(idCuenta);
        validarCuentaOperable(cuenta);

        cuenta.setSaldo(cuenta.getSaldo().add(dto.getMonto()));
        Cuenta actualizada = cuentaRepository.save(cuenta);

        log.info("Deposito id={} monto={} saldoNuevo={}", idCuenta, dto.getMonto(), actualizada.getSaldo());

        return CuentaResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public CuentaResponseDTO retirar(Long idCuenta, MontoRequestDTO dto) {
        Cuenta cuenta = buscarCuenta(idCuenta);
        validarCuentaOperable(cuenta);

        if (cuenta.getSaldo().compareTo(dto.getMonto()) < 0) {
            throw new BusinessRuleException("Saldo insuficiente");
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(dto.getMonto()));
        Cuenta actualizada = cuentaRepository.save(cuenta);

        log.info("Retiro id={} monto={} saldoNuevo={}", idCuenta, dto.getMonto(), actualizada.getSaldo());

        return CuentaResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public TransferenciaResponseDTO transferir(TransferenciaRequestDTO dto) {
        if (dto.getIdCuentaOrigen().equals(dto.getIdCuentaDestino())) {
            throw new BusinessRuleException("La cuenta origen y destino deben ser distintas");
        }

        Cuenta origen = buscarCuenta(dto.getIdCuentaOrigen());
        Cuenta destino = buscarCuenta(dto.getIdCuentaDestino());

        validarCuentaOperable(origen);
        validarCuentaOperable(destino);

        if (origen.getSaldo().compareTo(dto.getMonto()) < 0) {
            throw new BusinessRuleException("Saldo insuficiente en la cuenta origen");
        }

        origen.setSaldo(origen.getSaldo().subtract(dto.getMonto()));
        destino.setSaldo(destino.getSaldo().add(dto.getMonto()));

        cuentaRepository.save(origen);
        cuentaRepository.save(destino);

        log.info("Transferencia origen={} destino={} monto={}",
                origen.getIdCuenta(), destino.getIdCuenta(), dto.getMonto());

        return TransferenciaResponseDTO.toResponseDTO(origen, destino, dto.getMonto());
    }

    @Transactional
    public CuentaResponseDTO bloquearCuenta(Long idCuenta) {
        Cuenta cuenta = buscarCuenta(idCuenta);

        if (cuenta.getEstado() == EstadoCuenta.INACTIVA) {
            throw new BusinessRuleException("No se puede bloquear una cuenta inactiva");
        }
        if (cuenta.getEstado() == EstadoCuenta.BLOQUEADA) {
            throw new BusinessRuleException("La cuenta ya esta bloqueada");
        }

        cuenta.setEstado(EstadoCuenta.BLOQUEADA);
        Cuenta actualizada = cuentaRepository.save(cuenta);

        log.info("Cuenta bloqueada id={}", idCuenta);

        return CuentaResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public CuentaResponseDTO activarCuenta(Long idCuenta) {
        Cuenta cuenta = buscarCuenta(idCuenta);

        if (cuenta.getEstado() == EstadoCuenta.INACTIVA) {
            throw new BusinessRuleException("No se puede activar una cuenta inactiva");
        }
        if (cuenta.getEstado() == EstadoCuenta.ACTIVA) {
            throw new BusinessRuleException("La cuenta ya esta activa");
        }

        cuenta.setEstado(EstadoCuenta.ACTIVA);
        Cuenta actualizada = cuentaRepository.save(cuenta);

        log.info("Cuenta activada id={}", idCuenta);

        return CuentaResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public CuentaResponseDTO cerrarCuenta(Long idCuenta) {
        Cuenta cuenta = buscarCuenta(idCuenta);

        if (cuenta.getEstado() == EstadoCuenta.INACTIVA) {
            throw new BusinessRuleException("La cuenta ya esta cerrada");
        }
        if (cuenta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessRuleException("Solo se puede cerrar una cuenta con saldo cero");
        }

        cuenta.setEstado(EstadoCuenta.INACTIVA);
        Cuenta actualizada = cuentaRepository.save(cuenta);

        log.info("Cuenta cerrada id={}", idCuenta);

        return CuentaResponseDTO.toResponseDTO(actualizada);
    }

    private Cuenta buscarCuenta(Long idCuenta) {
        return cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
    }

    private void validarCuentaOperable(Cuenta cuenta) {
        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) {
            throw new BusinessRuleException(
                    "La cuenta id=" + cuenta.getIdCuenta() + " no esta activa (estado=" + cuenta.getEstado() + ")");
        }
    }

    private void validarClienteExiste(String rutCliente) {
        try {
            clienteFeignClient.obtenerCliente(rutCliente);
        } catch (FeignException.NotFound e) {
            throw new BusinessRuleException("El cliente con rut " + rutCliente + " no existe");
        } catch (FeignException e) {
            log.error("Error invocando ms-clientes para rut={}: {}", rutCliente, e.getMessage());
            throw new RemoteServiceException("No se pudo validar el cliente en ms-clientes");
        }
    }
}
