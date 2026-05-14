package com.novabank.mscuentas.controller;

import com.novabank.mscuentas.dto.request.CuentaRequestDTO;
import com.novabank.mscuentas.dto.request.MontoRequestDTO;
import com.novabank.mscuentas.dto.request.TransferenciaRequestDTO;
import com.novabank.mscuentas.dto.response.CuentaResponseDTO;
import com.novabank.mscuentas.dto.response.TransferenciaResponseDTO;
import com.novabank.mscuentas.model.EstadoCuenta;
import com.novabank.mscuentas.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "Gestion de cuentas bancarias y operaciones monetarias (deposito, retiro, transferencia)")
public class CuentaController {

    private final CuentaService cuentaService;


    @GetMapping
    @Operation(summary = "Lista todas las cuentas")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<CuentaResponseDTO>> obtenerCuentas() {
        return ResponseEntity.ok(cuentaService.obtenerCuentas());
    }

    @GetMapping("/{idCuenta}")
    @Operation(summary = "Obtiene una cuenta por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<CuentaResponseDTO> obtenerCuentaPorId(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(cuentaService.obtenerCuentaPorId(idCuenta));
    }

    @GetMapping("/cliente/{rutCliente}")
    @Operation(summary = "Lista cuentas de un cliente por rut")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<CuentaResponseDTO>> obtenerCuentasPorRutCliente(@PathVariable String rutCliente) {
        return ResponseEntity.ok(cuentaService.obtenerCuentasPorRutCliente(rutCliente));
    }

    @GetMapping("/cliente/{rutCliente}/activas")
    @Operation(summary = "Lista solo las cuentas ACTIVAS del cliente")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<CuentaResponseDTO>> obtenerActivasPorRutCliente(@PathVariable String rutCliente) {
        return ResponseEntity.ok(cuentaService.obtenerActivasPorRutCliente(rutCliente));
    }

    @GetMapping("/cliente/{rutCliente}/saldo-total")
    @Operation(summary = "Saldo total agregado de las cuentas ACTIVAS del cliente (valida cliente via Feign)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suma calculada"),
            @ApiResponse(responseCode = "400", description = "Cliente no existe en ms-clientes"),
            @ApiResponse(responseCode = "503", description = "ms-clientes no disponible")
    })
    public ResponseEntity<Map<String, Object>> saldoTotalActivoPorCliente(@PathVariable String rutCliente) {
        BigDecimal total = cuentaService.saldoTotalActivoPorCliente(rutCliente);
        return ResponseEntity.ok(Map.of("rutCliente", rutCliente, "saldoTotalActivo", total));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Lista cuentas filtrando por estado (ACTIVA, INACTIVA, BLOQUEADA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Estado invalido")
    })
    public ResponseEntity<List<CuentaResponseDTO>> obtenerPorEstado(@PathVariable EstadoCuenta estado) {
        return ResponseEntity.ok(cuentaService.obtenerCuentasPorEstado(estado));
    }

    @GetMapping("/fechas")
    @Operation(summary = "Lista cuentas creadas en un rango de fechas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos o desde > hasta")
    })
    public ResponseEntity<List<CuentaResponseDTO>> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(cuentaService.obtenerCuentasPorRangoFechas(desde, hasta));
    }

    @PostMapping
    @Operation(summary = "Crea una cuenta validando que el cliente exista en ms-clientes (Feign)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cuenta creada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o cliente no existe"),
            @ApiResponse(responseCode = "404", description = "Tipo de cuenta no existe"),
            @ApiResponse(responseCode = "409", description = "Numero de cuenta duplicado"),
            @ApiResponse(responseCode = "503", description = "ms-clientes no disponible")
    })
    public ResponseEntity<CuentaResponseDTO> crearCuenta(@Valid @RequestBody CuentaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.crearCuenta(dto));
    }

    @PutMapping("/{idCuenta}")
    @Operation(summary = "Actualiza una cuenta (no permitido si esta INACTIVA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o cuenta inactiva"),
            @ApiResponse(responseCode = "404", description = "Cuenta o tipo de cuenta no encontrado"),
            @ApiResponse(responseCode = "409", description = "Numero de cuenta duplicado")
    })
    public ResponseEntity<CuentaResponseDTO> actualizarCuenta(
            @PathVariable Long idCuenta,
            @Valid @RequestBody CuentaRequestDTO dto) {
        return ResponseEntity.ok(cuentaService.actualizarCuenta(idCuenta, dto));
    }

    @DeleteMapping("/{idCuenta}")
    @Operation(summary = "Elimina una cuenta (solo si tiene saldo 0)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada"),
            @ApiResponse(responseCode = "400", description = "Cuenta con saldo positivo"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long idCuenta) {
        cuentaService.eliminarCuenta(idCuenta);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idCuenta}/depositar")
    @Operation(summary = "Deposita un monto en la cuenta (solo si esta ACTIVA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deposito realizado"),
            @ApiResponse(responseCode = "400", description = "Monto invalido o cuenta no ACTIVA"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<CuentaResponseDTO> depositar(
            @PathVariable Long idCuenta,
            @Valid @RequestBody MontoRequestDTO dto) {
        return ResponseEntity.ok(cuentaService.depositar(idCuenta, dto));
    }

    @PostMapping("/{idCuenta}/retirar")
    @Operation(summary = "Retira un monto de la cuenta validando saldo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retiro realizado"),
            @ApiResponse(responseCode = "400", description = "Monto invalido, saldo insuficiente o cuenta no ACTIVA"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<CuentaResponseDTO> retirar(
            @PathVariable Long idCuenta,
            @Valid @RequestBody MontoRequestDTO dto) {
        return ResponseEntity.ok(cuentaService.retirar(idCuenta, dto));
    }

    @PostMapping("/transferencia")
    @Operation(summary = "Transferencia atomica entre dos cuentas ACTIVAS")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferencia realizada"),
            @ApiResponse(responseCode = "400", description = "Misma cuenta origen y destino, saldo insuficiente o cuentas no ACTIVAS"),
            @ApiResponse(responseCode = "404", description = "Cuenta origen o destino no encontrada")
    })
    public ResponseEntity<TransferenciaResponseDTO> transferir(@Valid @RequestBody TransferenciaRequestDTO dto) {
        return ResponseEntity.ok(cuentaService.transferir(dto));
    }

    @PatchMapping("/{idCuenta}/bloquear")
    @Operation(summary = "Bloquea una cuenta ACTIVA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta bloqueada"),
            @ApiResponse(responseCode = "400", description = "Cuenta ya bloqueada o inactiva"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<CuentaResponseDTO> bloquear(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(cuentaService.bloquearCuenta(idCuenta));
    }

    @PatchMapping("/{idCuenta}/activar")
    @Operation(summary = "Activa una cuenta BLOQUEADA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta activada"),
            @ApiResponse(responseCode = "400", description = "Cuenta ya activa o inactiva"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<CuentaResponseDTO> activar(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(cuentaService.activarCuenta(idCuenta));
    }

    @PatchMapping("/{idCuenta}/cerrar")
    @Operation(summary = "Cierra una cuenta (solo si saldo = 0)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta cerrada"),
            @ApiResponse(responseCode = "400", description = "Cuenta ya cerrada o saldo distinto a 0"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<CuentaResponseDTO> cerrar(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(cuentaService.cerrarCuenta(idCuenta));
    }
}
