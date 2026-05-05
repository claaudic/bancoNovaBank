package com.novabank.mstransacciones.controller;

import com.novabank.mstransacciones.dto.TransaccionRequestDTO;
import com.novabank.mstransacciones.dto.TransaccionResponseDTO;
import com.novabank.mstransacciones.service.TransaccionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService){
        this.transaccionService = transaccionService;
    }

    @PostMapping
    public ResponseEntity<TransaccionResponseDTO> crearTransaccion(@Valid @RequestBody TransaccionRequestDTO requestDTO){
        TransaccionResponseDTO transaccionGuardada = transaccionService.guardarTransaccion(requestDTO);

        return new ResponseEntity<>(transaccionGuardada, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerTodas(){
        return ResponseEntity.ok(transaccionService.obtenerTodosTransaccion());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionResponseDTO> obtenerPorId(@PathVariable Long id){

        return transaccionService.obtenerPorIdTransaccion(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransaccionResponseDTO> actualizar(@PathVariable Long id, @RequestBody TransaccionRequestDTO dto){
        return ResponseEntity.ok(transaccionService.actualizarTransaccion(id, dto));
    }
}
