package com.novabank.mscuentas.controller;

import com.novabank.mscuentas.dto.request.TarjetaRequestDTO;
import com.novabank.mscuentas.dto.response.TarjetaResponseDTO;
import com.novabank.mscuentas.model.EstadoTarjeta;
import com.novabank.mscuentas.service.TarjetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tarjetas")
@RequiredArgsConstructor
@Tag(name = "Tarjetas", description = "Tarjetas asociadas a cuentas bancarias")
public class TarjetaController {

    private final TarjetaService tarjetaService;

    @GetMapping
    @Operation(summary = "Lista todas las tarjetas")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<TarjetaResponseDTO>> obtenerTarjetas() {
        return ResponseEntity.ok(tarjetaService.obtenerTarjetas());
    }

    @GetMapping("/{idTarjeta}")
    @Operation(summary = "Obtiene una tarjeta por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
    })
    public ResponseEntity<TarjetaResponseDTO> obtenerTarjetaPorId(@PathVariable Long idTarjeta) {
        return ResponseEntity.ok(tarjetaService.obtenerTarjetaPorId(idTarjeta));
    }

    @GetMapping("/cuenta/{idCuenta}")
    @Operation(summary = "Lista tarjetas de una cuenta")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<TarjetaResponseDTO>> obtenerTarjetasPorIdCuenta(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(tarjetaService.obtenerTarjetasPorIdCuenta(idCuenta));
    }

    @GetMapping("/cliente/{rutCliente}")
    @Operation(summary = "Lista tarjetas de todas las cuentas de un cliente")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<TarjetaResponseDTO>> obtenerTarjetasPorRutCliente(@PathVariable String rutCliente) {
        return ResponseEntity.ok(tarjetaService.obtenerTarjetasPorRutCliente(rutCliente));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Lista tarjetas filtrando por estado (ACTIVA, INACTIVA, BLOQUEADA, VENCIDA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Estado invalido")
    })
    public ResponseEntity<List<TarjetaResponseDTO>> obtenerPorEstado(@PathVariable EstadoTarjeta estado) {
        return ResponseEntity.ok(tarjetaService.obtenerTarjetasPorEstado(estado));
    }

    @GetMapping("/vencidas")
    @Operation(summary = "Lista tarjetas con fecha de vencimiento pasada")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<TarjetaResponseDTO>> obtenerVencidas() {
        return ResponseEntity.ok(tarjetaService.obtenerTarjetasVencidas());
    }

    @PostMapping
    @Operation(summary = "Crea una tarjeta sobre una cuenta ACTIVA")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarjeta creada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o cuenta no ACTIVA"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "409", description = "Numero de tarjeta duplicado")
    })
    public ResponseEntity<TarjetaResponseDTO> crearTarjeta(@Valid @RequestBody TarjetaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarjetaService.crearTarjeta(dto));
    }

    @PutMapping("/{idTarjeta}")
    @Operation(summary = "Actualiza una tarjeta (no permitido si INACTIVA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o tarjeta INACTIVA"),
            @ApiResponse(responseCode = "404", description = "Tarjeta o cuenta no encontrada"),
            @ApiResponse(responseCode = "409", description = "Numero de tarjeta duplicado")
    })
    public ResponseEntity<TarjetaResponseDTO> actualizarTarjeta(
            @PathVariable Long idTarjeta,
            @Valid @RequestBody TarjetaRequestDTO dto) {
        return ResponseEntity.ok(tarjetaService.actualizarTarjeta(idTarjeta, dto));
    }

    @DeleteMapping("/{idTarjeta}")
    @Operation(summary = "Elimina una tarjeta")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarjeta eliminada"),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
    })
    public ResponseEntity<Void> eliminarTarjeta(@PathVariable Long idTarjeta) {
        tarjetaService.eliminarTarjeta(idTarjeta);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{idTarjeta}/bloquear")
    @Operation(summary = "Bloquea una tarjeta ACTIVA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta bloqueada"),
            @ApiResponse(responseCode = "400", description = "Tarjeta ya BLOQUEADA, INACTIVA o VENCIDA"),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
    })
    public ResponseEntity<TarjetaResponseDTO> bloquear(@PathVariable Long idTarjeta) {
        return ResponseEntity.ok(tarjetaService.bloquearTarjeta(idTarjeta));
    }

    @PatchMapping("/{idTarjeta}/activar")
    @Operation(summary = "Activa una tarjeta BLOQUEADA (no permitido si vencida o inactiva)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta activada"),
            @ApiResponse(responseCode = "400", description = "Tarjeta ya ACTIVA, INACTIVA o vencida"),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
    })
    public ResponseEntity<TarjetaResponseDTO> activar(@PathVariable Long idTarjeta) {
        return ResponseEntity.ok(tarjetaService.activarTarjeta(idTarjeta));
    }

    @PostMapping("/marcar-vencidas")
    @Operation(summary = "Batch: marca como VENCIDAS todas las tarjetas con fechaVencimiento pasada")
    @ApiResponse(responseCode = "200", description = "Devuelve cuantas se marcaron")
    public ResponseEntity<Map<String, Integer>> marcarVencidas() {
        int total = tarjetaService.marcarTarjetasVencidas();
        return ResponseEntity.ok(Map.of("tarjetasMarcadas", total));
    }
}
