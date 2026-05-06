package com.novabank.msclientes.controller;

import com.novabank.msclientes.dto.ClienteRequestDTO;
import com.novabank.msclientes.dto.ClienteResponseDTO;
import com.novabank.msclientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;


    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> obtenerClientes() {
        return ResponseEntity.ok(clienteService.obtenerClientes());
    }

    @GetMapping("/{rutCliente}")
    public ResponseEntity<ClienteResponseDTO> obtenerCliente(@PathVariable String rutCliente) {

        return ResponseEntity.ok(
                clienteService.obtenerClientePorRut(rutCliente)
        );
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crearCliente(
            @Valid @RequestBody ClienteRequestDTO dtoCliente) {
      return ResponseEntity.status(201).body(clienteService.crearCliente(dtoCliente));
    }

    @PutMapping("/{rutCliente}")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
            @PathVariable String rutCliente,
            @Valid @RequestBody ClienteRequestDTO dtoCliente) {

        return ResponseEntity.ok(
                clienteService.actualizarCliente(rutCliente, dtoCliente)
        );
    }

    @DeleteMapping("/{rutCliente}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable String rutCliente) {

        clienteService.eliminarCliente(rutCliente);

        return ResponseEntity.noContent().build();
    }


}

