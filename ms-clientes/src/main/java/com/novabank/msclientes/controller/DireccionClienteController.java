package com.novabank.msclientes.controller;

import com.novabank.msclientes.dto.DireccionClienteRequestDTO;
import com.novabank.msclientes.dto.DireccionClienteResponseDTO;
import com.novabank.msclientes.service.DireccionClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/direcciones")
@RequiredArgsConstructor
public class DireccionClienteController {

    private final DireccionClienteService direccionClienteService;


    @GetMapping
    public ResponseEntity<List<DireccionClienteResponseDTO>> obtenerDirecciones() {
        return ResponseEntity.ok(direccionClienteService.obtenerDirecciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DireccionClienteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(direccionClienteService.obtenerPorId(id));
    }

    @PostMapping("/{rutCliente}")
    public ResponseEntity<DireccionClienteResponseDTO> crearDireccion(
            @PathVariable String rutCliente,
            @Valid @RequestBody DireccionClienteRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(direccionClienteService.crearDireccion(rutCliente, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DireccionClienteResponseDTO> actualizarDireccion(
            @PathVariable Long id,
            @Valid @RequestBody DireccionClienteRequestDTO dto) {

        return ResponseEntity.ok(direccionClienteService.actualizarDireccion(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long id) {

        direccionClienteService.eliminarDireccion(id);

        return ResponseEntity.noContent().build();
    }
}
