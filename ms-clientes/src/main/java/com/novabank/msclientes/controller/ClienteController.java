package com.novabank.msclientes.controller;

import com.novabank.msclientes.dto.request.ClienteRequestDTO;
import com.novabank.msclientes.dto.response.ClienteResponseDTO;
import com.novabank.msclientes.model.Estado;
import com.novabank.msclientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.ok(clienteService.obtenerClientePorRut(rutCliente));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ClienteResponseDTO>> obtenerPorEstado(@PathVariable Estado estado) {
        return ResponseEntity.ok(clienteService.obtenerPorEstado(estado));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponseDTO>> buscarPorNombre(@RequestParam String texto) {
        return ResponseEntity.ok(clienteService.buscarPorNombre(texto));
    }

    @GetMapping("/profesion/{idProfesion}")
    public ResponseEntity<List<ClienteResponseDTO>> obtenerPorProfesion(@PathVariable Long idProfesion) {
        return ResponseEntity.ok(clienteService.obtenerPorProfesion(idProfesion));
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crearCliente(
            @Valid @RequestBody ClienteRequestDTO dtoCliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearCliente(dtoCliente));
    }

    @PutMapping("/{rutCliente}")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
            @PathVariable String rutCliente,
            @Valid @RequestBody ClienteRequestDTO dtoCliente) {
        return ResponseEntity.ok(clienteService.actualizarCliente(rutCliente, dtoCliente));
    }

    @PatchMapping("/{rutCliente}/activar")
    public ResponseEntity<ClienteResponseDTO> activar(@PathVariable String rutCliente) {
        return ResponseEntity.ok(clienteService.activarCliente(rutCliente));
    }

    @PatchMapping("/{rutCliente}/desactivar")
    public ResponseEntity<ClienteResponseDTO> desactivar(@PathVariable String rutCliente) {
        return ResponseEntity.ok(clienteService.desactivarCliente(rutCliente));
    }

    @DeleteMapping("/{rutCliente}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable String rutCliente) {
        clienteService.eliminarCliente(rutCliente);
        return ResponseEntity.noContent().build();
    }
}
