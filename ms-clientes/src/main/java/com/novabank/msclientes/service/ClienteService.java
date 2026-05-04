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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ProfesionRepository profesionRepository;

    public void guardarCliente (ClienteRequestDTO clienteEntrada) {
        Cliente cliente = clienteEntrada.toEntity();

        Profesion profesion = profesionRepository
                .findById(clienteEntrada.getIdProfesion())
                .orElseThrow();

        cliente.setProfesion(profesion);

        List<DireccionCliente> direcciones = clienteEntrada.getDirecciones()
                        .stream().map(DireccionClienteRequestDTO::toEntity).toList();
        cliente.setDireccionClientes(direcciones);
        clienteRepository.save(cliente);
    }

   public List<Cliente> obtenerClientes() {
        return clienteRepository.findAll();
   }

   public void eliminarCliente(String rutCliente) {
        clienteRepository.deleteById(rutCliente);
   }

    public ClienteResponseDTO actualizarCliente(String rutCliente, ClienteRequestDTO clienteActualizado) {

        Cliente cliente = clienteRepository.findById(rutCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setNombreCliente(clienteActualizado.getNombreCliente());
        cliente.setApellidoCliente(clienteActualizado.getApellidoCliente());
        cliente.setTelefonoCliente(clienteActualizado.getTelefonoCliente());
        cliente.setEmailCliente(clienteActualizado.getEmailCliente());

        clienteRepository.save(cliente);
        return ClienteResponseDTO.toResponseDTO(cliente);
    }
}
