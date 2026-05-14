package com.novabank.msorganizacion.controller;

import com.novabank.msorganizacion.dto.EjecutivoRequestDTO;
import com.novabank.msorganizacion.dto.EjecutivoResponseDTO;
import com.novabank.msorganizacion.service.EjecutivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ejecutivos")
@RequiredArgsConstructor
public class EjecutivoController {

    private final EjecutivoService ejecutivoService;

    @PostMapping
    public ResponseEntity<EjecutivoResponseDTO> crear(@RequestBody EjecutivoRequestDTO dto) {
        return new ResponseEntity<>(ejecutivoService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EjecutivoResponseDTO> actualizar(@PathVariable Long id, @RequestBody EjecutivoRequestDTO dto) {
        return ResponseEntity.ok(ejecutivoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ejecutivoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sucursal/{idSucursal}")
    public ResponseEntity<List<EjecutivoResponseDTO>> listarPorSucursal(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(ejecutivoService.listarPorSucursal(idSucursal));
    }
}