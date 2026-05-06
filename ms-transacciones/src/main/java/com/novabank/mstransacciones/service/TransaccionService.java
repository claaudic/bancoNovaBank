package com.novabank.mstransacciones.service;

import com.novabank.mstransacciones.dto.TransaccionRequestDTO;
import com.novabank.mstransacciones.dto.TransaccionResponseDTO;
import com.novabank.mstransacciones.model.Estado;
import com.novabank.mstransacciones.model.Transaccion;
import com.novabank.mstransacciones.repository.TransaccionRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

    public TransaccionResponseDTO crearTransaccion(TransaccionRequestDTO transaccionRequestDTO){
        if (transaccionRequestDTO.getMontoTransaccion().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El monto debe ser mayor a cero"
            );
        }
        Transaccion transaccion = transaccionRequestDTO.transaccionEntity();

        transaccion.setFechaTransaccion(LocalDateTime.now());
        transaccion.setEstado(Estado.COMPLETADA);

        Transaccion guardada = transaccionRepository.save(transaccion);
        return TransaccionResponseDTO.toResponseDTO(guardada);
    }

    public List<TransaccionResponseDTO> obtenerTransacciones(){
        return transaccionRepository.findAll()
                .stream()
                .map(TransaccionResponseDTO::toResponseDTO).toList();
    }

    public TransaccionResponseDTO obtenerTransaccionPorId(Long idTransaccion) {

        Transaccion transaccion = transaccionRepository.findById(idTransaccion)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaccion no encontrada"
                ));

        return TransaccionResponseDTO.toResponseDTO(transaccion);
    }


    public TransaccionResponseDTO actualizarTransaccion(
            Long idTransaccion,
            TransaccionRequestDTO dto) {

        Transaccion transaccion = transaccionRepository.findById(idTransaccion)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaccion no encontrada"
                ));

        if (dto.getMontoTransaccion().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El monto debe ser mayor a cero"
            );
        }

        transaccion.setIdCuentaOrigen(dto.getIdCuentaOrigen());
        transaccion.setIdCuentaDestino(dto.getIdCuentaDestino());
        transaccion.setTipoTransaccion(dto.getTipoTransaccion());
        transaccion.setMontoTransaccion(dto.getMontoTransaccion());
        transaccion.setDescripcion(dto.getDescripcion());

        Transaccion actualizada = transaccionRepository.save(transaccion);

        return TransaccionResponseDTO.toResponseDTO(actualizada);
    }

    public void eliminarTransaccion(Long idTransaccion) {

        if (!transaccionRepository.existsById(idTransaccion)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Transaccion no encontrada"
            );
        }

        transaccionRepository.deleteById(idTransaccion);
    }

}