package com.novabank.mstransacciones.controller;

import com.novabank.mstransacciones.dto.request.TransaccionRequestDTO;
import com.novabank.mstransacciones.dto.response.TransaccionResponseDTO;
import com.novabank.mstransacciones.model.Estado;
import com.novabank.mstransacciones.model.TipoTransaccion;
import com.novabank.mstransacciones.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;


    @GetMapping
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerTransacciones() {
        return ResponseEntity.ok(transaccionService.obtenerTransacciones());
    }

    @GetMapping("/{idTransaccion}")
    public ResponseEntity<TransaccionResponseDTO> obtenerTransaccionPorId(@PathVariable Long idTransaccion) {
        return ResponseEntity.ok(transaccionService.obtenerTransaccionPorId(idTransaccion));
    }

    @GetMapping("/cuenta/{idCuenta}")
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerPorCuenta(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(transaccionService.obtenerPorCuenta(idCuenta));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerPorEstado(@PathVariable Estado estado) {
        return ResponseEntity.ok(transaccionService.obtenerPorEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerPorTipo(@PathVariable TipoTransaccion tipo) {
        return ResponseEntity.ok(transaccionService.obtenerPorTipo(tipo));
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(transaccionService.obtenerPorRangoFechas(desde, hasta));
    }

    @PostMapping
    public ResponseEntity<TransaccionResponseDTO> crearTransaccion(
            @Valid @RequestBody TransaccionRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transaccionService.crearTransaccion(requestDTO));
    }

    @PutMapping("/{idTransaccion}")
    public ResponseEntity<TransaccionResponseDTO> actualizarTransaccion(
            @PathVariable Long idTransaccion,
            @Valid @RequestBody TransaccionRequestDTO dto) {
        return ResponseEntity.ok(transaccionService.actualizarTransaccion(idTransaccion, dto));
    }

    @DeleteMapping("/{idTransaccion}")
    public ResponseEntity<Void> eliminarTransaccion(@PathVariable Long idTransaccion) {
        transaccionService.eliminarTransaccion(idTransaccion);
        return ResponseEntity.noContent().build();
    }
}
