package com.novabank.mstransacciones.controller;

import com.novabank.mstransacciones.dto.TransaccionRequestDTO;
import com.novabank.mstransacciones.dto.TransaccionResponseDTO;
import com.novabank.mstransacciones.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;


    @GetMapping
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerTransacciones(){
        return ResponseEntity.ok(transaccionService.obtenerTransacciones());
    }

    @GetMapping("/{idTransaccion}")
    public ResponseEntity<TransaccionResponseDTO> obtenerTransaccionPorId(
            @PathVariable Long idTransaccion) {

        return ResponseEntity.ok(
                transaccionService.obtenerTransaccionPorId(idTransaccion)
        );
    }

    @PostMapping
    public ResponseEntity<TransaccionResponseDTO> crearTransaccion(@Valid @RequestBody TransaccionRequestDTO requestDTO){
        TransaccionResponseDTO transaccionGuardada = transaccionService.crearTransaccion(requestDTO);

        return new ResponseEntity<>(transaccionGuardada, HttpStatus.CREATED);
    }
    @PutMapping("/{idTransaccion}")
    public ResponseEntity<TransaccionResponseDTO> actualizarTransaccion(
            @PathVariable Long idTransaccion,
            @Valid @RequestBody TransaccionRequestDTO dto) {

        return ResponseEntity.ok(
                transaccionService.actualizarTransaccion(idTransaccion, dto)
        );
    }
    @DeleteMapping("/{idTransaccion}")
    public ResponseEntity<Void> eliminarTransaccion(
            @PathVariable Long idTransaccion) {

        transaccionService.eliminarTransaccion(idTransaccion);

        return ResponseEntity.noContent().build();
    }


}
