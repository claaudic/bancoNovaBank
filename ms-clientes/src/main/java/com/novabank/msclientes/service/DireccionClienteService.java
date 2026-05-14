package com.novabank.msclientes.service;

import com.novabank.msclientes.dto.request.DireccionClienteRequestDTO;
import com.novabank.msclientes.dto.response.DireccionClienteResponseDTO;
import com.novabank.msclientes.exception.BusinessRuleException;
import com.novabank.msclientes.exception.DuplicateResourceException;
import com.novabank.msclientes.exception.ResourceNotFoundException;
import com.novabank.msclientes.model.Cliente;
import com.novabank.msclientes.model.DireccionCliente;
import com.novabank.msclientes.model.Estado;
import com.novabank.msclientes.model.TipoDireccion;
import com.novabank.msclientes.repository.ClienteRepository;
import com.novabank.msclientes.repository.DireccionClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DireccionClienteService {

    private final DireccionClienteRepository direccionClienteRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public DireccionClienteResponseDTO crearDireccion(String rutCliente, DireccionClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(rutCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        if (cliente.getEstado() == Estado.INACTIVO) {
            throw new BusinessRuleException("No se puede agregar direccion a un cliente inactivo");
        }

        if (direccionClienteRepository.existsByClienteRutClienteAndCalleAndNumeroAndDepta(
                rutCliente, dto.getCalle(), dto.getNumero(), dto.getDepta())) {
            throw new DuplicateResourceException(
                    "El cliente ya tiene una direccion registrada con calle, numero y depto identicos");
        }

        DireccionCliente direccion = dto.toEntity();
        direccion.setCliente(cliente);

        DireccionCliente guardada = direccionClienteRepository.save(direccion);

        log.info("Direccion creada id={} para rutCliente={}", guardada.getId(), rutCliente);

        return DireccionClienteResponseDTO.toDireccionClienteResponseDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<DireccionClienteResponseDTO> obtenerDirecciones() {
        return direccionClienteRepository.findAll()
                .stream()
                .map(DireccionClienteResponseDTO::toDireccionClienteResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DireccionClienteResponseDTO obtenerPorId(Long id) {
        return DireccionClienteResponseDTO.toDireccionClienteResponseDTO(buscarDireccion(id));
    }

    @Transactional(readOnly = true)
    public List<DireccionClienteResponseDTO> obtenerPorRutCliente(String rutCliente) {
        if (!clienteRepository.existsById(rutCliente)) {
            throw new ResourceNotFoundException("Cliente no encontrado");
        }
        return direccionClienteRepository.findByClienteRutCliente(rutCliente)
                .stream()
                .map(DireccionClienteResponseDTO::toDireccionClienteResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DireccionClienteResponseDTO> obtenerPorCiudad(String ciudad) {
        return direccionClienteRepository.findByCiudadIgnoreCase(ciudad)
                .stream()
                .map(DireccionClienteResponseDTO::toDireccionClienteResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DireccionClienteResponseDTO> obtenerPorTipo(TipoDireccion tipoDireccion) {
        return direccionClienteRepository.findByTipoDireccion(tipoDireccion)
                .stream()
                .map(DireccionClienteResponseDTO::toDireccionClienteResponseDTO)
                .toList();
    }

    @Transactional
    public DireccionClienteResponseDTO actualizarDireccion(Long id, DireccionClienteRequestDTO dto) {
        DireccionCliente direccion = buscarDireccion(id);

        if (direccion.getCliente().getEstado() == Estado.INACTIVO) {
            throw new BusinessRuleException("No se puede actualizar direccion de un cliente inactivo");
        }

        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());
        direccion.setDepta(dto.getDepta());
        direccion.setTipoDireccion(dto.getTipoDireccion());
        direccion.setCiudad(dto.getCiudad());

        DireccionCliente actualizada = direccionClienteRepository.save(direccion);

        log.info("Direccion actualizada id={}", id);

        return DireccionClienteResponseDTO.toDireccionClienteResponseDTO(actualizada);
    }

    @Transactional
    public void eliminarDireccion(Long id) {
        if (!direccionClienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Direccion no encontrada");
        }

        log.info("Eliminando direccion id={}", id);

        direccionClienteRepository.deleteById(id);
    }

    private DireccionCliente buscarDireccion(Long id) {
        return direccionClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion no encontrada"));
    }
}
