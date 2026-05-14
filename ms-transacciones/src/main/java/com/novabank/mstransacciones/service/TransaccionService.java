package com.novabank.mstransacciones.service;

import com.novabank.mstransacciones.client.CuentaFeignClient;
import com.novabank.mstransacciones.dto.request.MontoFeignRequest;
import com.novabank.mstransacciones.dto.request.TransaccionRequestDTO;
import com.novabank.mstransacciones.dto.response.CuentaResponseDTO;
import com.novabank.mstransacciones.dto.response.TransaccionResponseDTO;
import com.novabank.mstransacciones.exception.BusinessRuleException;
import com.novabank.mstransacciones.exception.RemoteServiceException;
import com.novabank.mstransacciones.exception.ResourceNotFoundException;
import com.novabank.mstransacciones.model.Estado;
import com.novabank.mstransacciones.model.TipoTransaccion;
import com.novabank.mstransacciones.model.Transaccion;
import com.novabank.mstransacciones.repository.TransaccionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransaccionService {

    private static final String ESTADO_CUENTA_ACTIVA = "ACTIVA";

    private final TransaccionRepository transaccionRepository;
    private final CuentaFeignClient cuentaFeignClient;

    @Transactional
    public TransaccionResponseDTO crearTransaccion(TransaccionRequestDTO dto) {
        if (dto.getTipoTransaccion() == TipoTransaccion.TRANSFERENCIA
                && dto.getIdCuentaOrigen().equals(dto.getIdCuentaDestino())) {
            throw new BusinessRuleException("La cuenta origen y destino deben ser distintas en una transferencia");
        }

        CuentaResponseDTO origen = obtenerCuentaRemota(dto.getIdCuentaOrigen(), "origen");
        validarCuentaActiva(origen, "origen");

        if (dto.getTipoTransaccion() == TipoTransaccion.TRANSFERENCIA
                || dto.getTipoTransaccion() == TipoTransaccion.PAGO) {
            CuentaResponseDTO destino = obtenerCuentaRemota(dto.getIdCuentaDestino(), "destino");
            validarCuentaActiva(destino, "destino");
        }

        Transaccion transaccion = dto.toEntity();
        transaccion.setFechaTransaccion(LocalDateTime.now());
        transaccion.setEstado(Estado.PENDIENTE);

        Transaccion guardada = transaccionRepository.save(transaccion);

        try {
            aplicarOperacionRemota(dto);
            guardada.setEstado(Estado.COMPLETADA);
            transaccionRepository.save(guardada);

            log.info("Transaccion completada id={} tipo={} monto={} origen={} destino={}",
                    guardada.getIdTransaccion(), guardada.getTipoTransaccion(),
                    guardada.getMontoTransaccion(), guardada.getIdCuentaOrigen(),
                    guardada.getIdCuentaDestino());

            return TransaccionResponseDTO.toResponseDTO(guardada);
        } catch (FeignException e) {
            guardada.setEstado(Estado.RECHAZADA);
            transaccionRepository.save(guardada);
            log.warn("Transaccion rechazada id={}: {}", guardada.getIdTransaccion(), e.getMessage());
            throw new BusinessRuleException("La operacion fue rechazada por ms-cuentas: " + extraerMensaje(e));
        }
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponseDTO> obtenerTransacciones() {
        return transaccionRepository.findAll()
                .stream()
                .map(TransaccionResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransaccionResponseDTO obtenerTransaccionPorId(Long idTransaccion) {
        return TransaccionResponseDTO.toResponseDTO(buscarTransaccion(idTransaccion));
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponseDTO> obtenerPorCuenta(Long idCuenta) {
        log.debug("Buscando transacciones para cuenta id={}", idCuenta);
        return transaccionRepository.findByIdCuentaOrigenOrIdCuentaDestino(idCuenta, idCuenta)
                .stream()
                .map(TransaccionResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponseDTO> obtenerPorEstado(Estado estado) {
        return transaccionRepository.findByEstado(estado)
                .stream()
                .map(TransaccionResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponseDTO> obtenerPorTipo(TipoTransaccion tipo) {
        return transaccionRepository.findByTipoTransaccion(tipo)
                .stream()
                .map(TransaccionResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponseDTO> obtenerPorRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        if (desde == null || hasta == null) {
            throw new BusinessRuleException("Las fechas desde y hasta son obligatorias");
        }
        if (desde.isAfter(hasta)) {
            throw new BusinessRuleException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        return transaccionRepository.findByFechaTransaccionBetween(desde, hasta)
                .stream()
                .map(TransaccionResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional
    public TransaccionResponseDTO actualizarTransaccion(Long idTransaccion, TransaccionRequestDTO dto) {
        Transaccion transaccion = buscarTransaccion(idTransaccion);

        if (transaccion.getEstado() == Estado.COMPLETADA) {
            throw new BusinessRuleException("No se puede actualizar una transaccion COMPLETADA");
        }

        transaccion.setIdCuentaOrigen(dto.getIdCuentaOrigen());
        transaccion.setIdCuentaDestino(dto.getIdCuentaDestino());
        transaccion.setTipoTransaccion(dto.getTipoTransaccion());
        transaccion.setMontoTransaccion(dto.getMontoTransaccion());
        transaccion.setDescripcion(dto.getDescripcion());

        Transaccion actualizada = transaccionRepository.save(transaccion);

        log.info("Transaccion actualizada id={}", idTransaccion);

        return TransaccionResponseDTO.toResponseDTO(actualizada);
    }

    @Transactional
    public void eliminarTransaccion(Long idTransaccion) {
        Transaccion transaccion = buscarTransaccion(idTransaccion);

        if (transaccion.getEstado() == Estado.COMPLETADA) {
            throw new BusinessRuleException("No se puede eliminar una transaccion COMPLETADA");
        }

        log.info("Eliminando transaccion id={}", idTransaccion);

        transaccionRepository.delete(transaccion);
    }

    private Transaccion buscarTransaccion(Long idTransaccion) {
        return transaccionRepository.findById(idTransaccion)
                .orElseThrow(() -> new ResourceNotFoundException("Transaccion no encontrada"));
    }

    private CuentaResponseDTO obtenerCuentaRemota(Long idCuenta, String rol) {
        try {
            return cuentaFeignClient.obtenerCuenta(idCuenta);
        } catch (FeignException.NotFound e) {
            throw new BusinessRuleException("La cuenta " + rol + " (id=" + idCuenta + ") no existe");
        } catch (FeignException e) {
            log.error("Error consultando cuenta {} id={}: {}", rol, idCuenta, e.getMessage());
            throw new RemoteServiceException("No se pudo consultar la cuenta " + rol + " en ms-cuentas");
        }
    }

    private void validarCuentaActiva(CuentaResponseDTO cuenta, String rol) {
        if (!ESTADO_CUENTA_ACTIVA.equals(cuenta.getEstado())) {
            throw new BusinessRuleException(
                    "La cuenta " + rol + " no esta ACTIVA (estado=" + cuenta.getEstado() + ")");
        }
    }

    private void aplicarOperacionRemota(TransaccionRequestDTO dto) {
        MontoFeignRequest monto = new MontoFeignRequest(dto.getMontoTransaccion());

        switch (dto.getTipoTransaccion()) {
            case DEPOSITO -> cuentaFeignClient.depositar(dto.getIdCuentaOrigen(), monto);
            case RETIRO, PAGO -> cuentaFeignClient.retirar(dto.getIdCuentaOrigen(), monto);
            case TRANSFERENCIA -> {
                cuentaFeignClient.retirar(dto.getIdCuentaOrigen(), monto);
                cuentaFeignClient.depositar(dto.getIdCuentaDestino(), monto);
            }
        }
    }

    private String extraerMensaje(FeignException e) {
        String body = e.contentUTF8();
        return body != null && !body.isBlank() ? body : e.getMessage();
    }
}
