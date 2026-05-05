package com.novabank.msclientes.service;

import com.novabank.msclientes.dto.DireccionClienteRequestDTO;
import com.novabank.msclientes.dto.DireccionClienteResponseDTO;
import com.novabank.msclientes.model.Cliente;
import com.novabank.msclientes.model.DireccionCliente;
import com.novabank.msclientes.repository.ClienteRepository;
import com.novabank.msclientes.repository.DireccionClienteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DireccionClienteService {

    private final DireccionClienteRepository direccionClienteRepository;
    private final ClienteRepository clienteRepository;

    public DireccionClienteResponseDTO crearDireccion(String rutCliente, DireccionClienteRequestDTO direccionClienteRequestDTO) {
        Cliente cliente = clienteRepository.findById(rutCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        DireccionCliente direccion = direccionClienteRequestDTO.toEntity();

        direccion.setCliente(cliente);

        DireccionCliente guardada = direccionClienteRepository.save(direccion);

        return DireccionClienteResponseDTO.toDireccionClienteResponseDTO(guardada);
    }

    public List<DireccionClienteResponseDTO> obtenerDirecciones() {
        return direccionClienteRepository.findAll()
                .stream()
                .map(DireccionClienteResponseDTO::toDireccionClienteResponseDTO)
                .toList();
    }


    public DireccionClienteResponseDTO obtenerPorId(Long id) {

        DireccionCliente direccion = direccionClienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Direccion no encontrada"));

        return DireccionClienteResponseDTO.toDireccionClienteResponseDTO(direccion);
    }

    public DireccionClienteResponseDTO actualizarDireccion(Long id, DireccionClienteRequestDTO dto) {

        DireccionCliente direccion = direccionClienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Direccion no encontrada"));

        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());
        direccion.setDepta(dto.getDepta());
        direccion.setTipoDireccion(dto.getTipoDireccion());
        direccion.setCiudad(dto.getCiudad());

        DireccionCliente actualizada = direccionClienteRepository.save(direccion);

        return DireccionClienteResponseDTO.toDireccionClienteResponseDTO(actualizada);
    }

    public void eliminarDireccion(Long id) {

        if (!direccionClienteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Direccion no encontrada");
        }

        direccionClienteRepository.deleteById(id);
    }


}
