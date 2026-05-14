package com.novabank.msclientes.service;

import com.novabank.msclientes.dto.request.ClienteRequestDTO;
import com.novabank.msclientes.dto.request.DireccionClienteRequestDTO;
import com.novabank.msclientes.dto.response.ClienteResponseDTO;
import com.novabank.msclientes.exception.BusinessRuleException;
import com.novabank.msclientes.exception.DuplicateResourceException;
import com.novabank.msclientes.exception.ResourceNotFoundException;
import com.novabank.msclientes.model.Cliente;
import com.novabank.msclientes.model.DireccionCliente;
import com.novabank.msclientes.model.Estado;
import com.novabank.msclientes.model.Profesion;
import com.novabank.msclientes.repository.ClienteRepository;
import com.novabank.msclientes.repository.ProfesionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ProfesionRepository profesionRepository;

    @Transactional
    public ClienteResponseDTO crearCliente(ClienteRequestDTO clienteEntrada) {
        if (clienteRepository.existsById(clienteEntrada.getRutCliente())) {
            throw new DuplicateResourceException("El cliente ya existe");
        }

        if (clienteRepository.existsByNumeroSerie(clienteEntrada.getNumeroSerie())) {
            throw new DuplicateResourceException("El numero de serie ya esta registrado");
        }

        if (clienteEntrada.getEmailCliente() != null
                && clienteRepository.existsByEmailCliente(clienteEntrada.getEmailCliente())) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }

        Profesion profesion = profesionRepository.findById(clienteEntrada.getIdProfesion())
                .orElseThrow(() -> new ResourceNotFoundException("Profesion no existe"));

        Cliente cliente = clienteEntrada.toEntity();
        cliente.setProfesion(profesion);
        cliente.setEstado(Estado.ACTIVO);
        cliente.setFechaCreacion(LocalDateTime.now());

        List<DireccionCliente> direcciones = clienteEntrada.getDirecciones() == null
                ? List.of()
                : clienteEntrada.getDirecciones().stream()
                    .map(DireccionClienteRequestDTO::toEntity)
                    .toList();

        direcciones.forEach(direccion -> direccion.setCliente(cliente));
        cliente.setDireccionClientes(direcciones);

        Cliente guardado = clienteRepository.save(cliente);

        log.info("Cliente creado rut={} nombre={}", guardado.getRutCliente(), guardado.getNombreCliente());

        return ClienteResponseDTO.toResponseDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> obtenerClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorRut(String rutCliente) {
        return ClienteResponseDTO.toResponseDTO(buscarCliente(rutCliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> obtenerPorEstado(Estado estado) {
        return clienteRepository.findByEstado(estado)
                .stream()
                .map(ClienteResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> buscarPorNombre(String texto) {
        log.debug("Buscando clientes por nombre o apellido que contiene: {}", texto);
        return clienteRepository
                .findByNombreClienteContainingIgnoreCaseOrApellidoClienteContainingIgnoreCase(texto, texto)
                .stream()
                .map(ClienteResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> obtenerPorProfesion(Long idProfesion) {
        if (!profesionRepository.existsById(idProfesion)) {
            throw new ResourceNotFoundException("Profesion no existe");
        }
        return clienteRepository.findByProfesionIdProfesion(idProfesion)
                .stream()
                .map(ClienteResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional
    public void eliminarCliente(String rutCliente) {
        Cliente cliente = buscarCliente(rutCliente);

        log.info("Eliminando cliente rut={}", rutCliente);

        clienteRepository.delete(cliente);
    }

    @Transactional
    public ClienteResponseDTO actualizarCliente(String rutCliente, ClienteRequestDTO clienteActualizado) {
        Cliente cliente = buscarCliente(rutCliente);

        if (cliente.getEstado() == Estado.INACTIVO) {
            throw new BusinessRuleException("No se puede actualizar un cliente inactivo");
        }

        if (clienteActualizado.getEmailCliente() != null
                && !clienteActualizado.getEmailCliente().equals(cliente.getEmailCliente())
                && clienteRepository.existsByEmailCliente(clienteActualizado.getEmailCliente())) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }

        cliente.setNombreCliente(clienteActualizado.getNombreCliente());
        cliente.setApellidoCliente(clienteActualizado.getApellidoCliente());
        cliente.setTelefonoCliente(clienteActualizado.getTelefonoCliente());
        cliente.setEmailCliente(clienteActualizado.getEmailCliente());

        Cliente actualizado = clienteRepository.save(cliente);

        log.info("Cliente actualizado rut={}", rutCliente);

        return ClienteResponseDTO.toResponseDTO(actualizado);
    }

    @Transactional
    public ClienteResponseDTO activarCliente(String rutCliente) {
        Cliente cliente = buscarCliente(rutCliente);

        if (cliente.getEstado() == Estado.ACTIVO) {
            throw new BusinessRuleException("El cliente ya esta activo");
        }

        cliente.setEstado(Estado.ACTIVO);
        Cliente actualizado = clienteRepository.save(cliente);

        log.info("Cliente activado rut={}", rutCliente);

        return ClienteResponseDTO.toResponseDTO(actualizado);
    }

    @Transactional
    public ClienteResponseDTO desactivarCliente(String rutCliente) {
        Cliente cliente = buscarCliente(rutCliente);

        if (cliente.getEstado() == Estado.INACTIVO) {
            throw new BusinessRuleException("El cliente ya esta inactivo");
        }

        cliente.setEstado(Estado.INACTIVO);
        Cliente actualizado = clienteRepository.save(cliente);

        log.info("Cliente desactivado rut={}", rutCliente);

        return ClienteResponseDTO.toResponseDTO(actualizado);
    }

    private Cliente buscarCliente(String rutCliente) {
        return clienteRepository.findById(rutCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }
}
