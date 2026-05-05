package com.novabank.mstransacciones.service;

import com.novabank.mstransacciones.dto.TransaccionRequestDTO;
import com.novabank.mstransacciones.dto.TransaccionResponseDTO;
import com.novabank.mstransacciones.model.Transaccion;
import com.novabank.mstransacciones.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

    private TransaccionResponseDTO mapToDTO(Transaccion t) {
        return new TransaccionResponseDTO(t.getIdTransaccion(), t.getIdCuentaOrigen(), t.getIdCuentaDestino(),
                t.getTipoTransaccion(), t.getMontoTransaccion(), t.getFechaTransaccion(), t.getDescripcion(), t.getEstado());
    }
    public List<TransaccionResponseDTO> obtenerTodosTransaccion(){
        return transaccionRepository.findAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<TransaccionResponseDTO> obtenerPorIdTransaccion(Long id){
        return transaccionRepository.findById(id).map(this::mapToDTO);
    }

    public TransaccionResponseDTO guardarTransaccion(TransaccionRequestDTO transaccionEntrada){
        Transaccion transaccion = transaccionEntrada.transaccionEntity();
        Transaccion guardada = transaccionRepository.save(transaccion);
        return TransaccionResponseDTO.toResponseDTO(guardada);
    }

    public TransaccionResponseDTO actualizarTransaccion(Long idTransaccion, TransaccionRequestDTO TransaccionNueva){
        Transaccion transaccionExistente = transaccionRepository.findById(idTransaccion)
                .orElseThrow(() -> new RuntimeException("Error: la transaccion con ID"+ idTransaccion + "no existe."));
        transaccionExistente.setIdCuentaOrigen(TransaccionNueva.getIdCuentaOrigen());
        transaccionExistente.setIdCuentaDestino(TransaccionNueva.getIdCuentaDestino());
        transaccionExistente.setTipoTransaccion(TransaccionNueva.getTipoTransaccion());
        transaccionExistente.setMontoTransaccion(TransaccionNueva.getMontoTransaccion());
        transaccionExistente.setDescripcion(TransaccionNueva.getDescripcion());

        transaccionRepository.save(transaccionExistente);
        return TransaccionResponseDTO.toResponseDTO(transaccionExistente);
    }

}