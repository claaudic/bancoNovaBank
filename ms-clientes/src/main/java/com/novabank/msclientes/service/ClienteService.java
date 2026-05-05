package com.novabank.msclientes.service;

import com.novabank.msclientes.dto.ClienteRequestDTO;
import com.novabank.msclientes.dto.ClienteResponseDTO;
import com.novabank.msclientes.dto.DireccionClienteRequestDTO;
import com.novabank.msclientes.model.Cliente;
import com.novabank.msclientes.model.DireccionCliente;
import com.novabank.msclientes.model.Profesion;
import com.novabank.msclientes.repository.ClienteRepository;
import com.novabank.msclientes.repository.ProfesionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ProfesionRepository profesionRepository;

    public ClienteResponseDTO guardarCliente(ClienteRequestDTO clienteEntrada) {

        Cliente cliente = clienteEntrada.toEntity();

        Optional<Cliente> clienteExiste = clienteRepository.findById(clienteEntrada.getRutCliente());

        if (clienteExiste.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El cliente ya existe");
        }

        Profesion profesion = profesionRepository
                .findById(clienteEntrada.getIdProfesion())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Profesion no existe"));

        cliente.setProfesion(profesion);

        List<DireccionCliente> direcciones = clienteEntrada.getDirecciones()
                .stream()
                .map(DireccionClienteRequestDTO::toEntity)
                .toList();

        direcciones.forEach(direccion -> direccion.setCliente(cliente));

        cliente.setDireccionClientes(direcciones);

        Cliente guardado = clienteRepository.save(cliente);

        return ClienteResponseDTO.toResponseDTO(guardado);
    }

    public List<ClienteResponseDTO> obtenerClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteResponseDTO::toResponseDTO)
                .toList();
    }

   public ClienteResponseDTO obtenerClientePorRut(String rutCliente) {

        Cliente cliente = clienteRepository.findById(rutCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        return ClienteResponseDTO.toResponseDTO(cliente);
    }

    public void eliminarCliente(String rutCliente) {

        Cliente cliente = clienteRepository.findById(rutCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        clienteRepository.delete(cliente);
    }

    public ClienteResponseDTO actualizarCliente(String rutCliente, ClienteRequestDTO clienteActualizado) {

        Cliente cliente = clienteRepository.findById(rutCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        cliente.setNombreCliente(clienteActualizado.getNombreCliente());
        cliente.setApellidoCliente(clienteActualizado.getApellidoCliente());
        cliente.setTelefonoCliente(clienteActualizado.getTelefonoCliente());
        cliente.setEmailCliente(clienteActualizado.getEmailCliente());

        Cliente actualizado = clienteRepository.save(cliente);

        return ClienteResponseDTO.toResponseDTO(actualizado);
    }
}
