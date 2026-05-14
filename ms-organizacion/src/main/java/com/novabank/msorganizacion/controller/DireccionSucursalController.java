package com.novabank.msorganizacion.controller;

import com.novabank.msorganizacion.dto.DireccionSucursalRequestDTO;
import com.novabank.msorganizacion.dto.DireccionSucursalResponseDTO;
import com.novabank.msorganizacion.service.DireccionSucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sucursalcontroller")
@RequiredArgsConstructor
public class DireccionSucursalController {

    private final DireccionSucursalService direccionService;

    @PostMapping
    public ResponseEntity<DireccionSucursalResponseDTO> crear(@RequestBody DireccionSucursalRequestDTO dto) {
        return new ResponseEntity<>(direccionService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DireccionSucursalResponseDTO> actualizar(@PathVariable Long id, @RequestBody DireccionSucursalRequestDTO dto) {
        return ResponseEntity.ok(direccionService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        direccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sucursal/{idSucursal}")
    public ResponseEntity<List<DireccionSucursalResponseDTO>> listarPorSucursal(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(direccionService.listarPorSucursal(idSucursal));
    }
}