package com.novabank.mstransacciones.controller;

import com.novabank.mstransacciones.dto.request.TransaccionRequestDTO;
import com.novabank.mstransacciones.dto.response.TransaccionResponseDTO;
import com.novabank.mstransacciones.model.Estado;
import com.novabank.mstransacciones.model.TipoTransaccion;
import com.novabank.mstransacciones.service.TransaccionService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
@Tag(name = "Transacciones", description = "Registro y orquestacion de operaciones financieras via Feign a ms-cuentas")
public class TransaccionController {

    private final TransaccionService transaccionService;


    @GetMapping
    @Operation(summary = "Lista todas las transacciones")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerTransacciones() {
        return ResponseEntity.ok(transaccionService.obtenerTransacciones());
    }

    @GetMapping("/{idTransaccion}")
    @Operation(summary = "Obtiene una transaccion por id con enlaces HATEOAS")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaccion encontrada"),
            @ApiResponse(responseCode = "404", description = "Transaccion no encontrada")
    })
    public ResponseEntity<TransaccionResponseDTO> obtenerTransaccionPorId(@PathVariable Long idTransaccion) {
        TransaccionResponseDTO dto = transaccionService.obtenerTransaccionPorId(idTransaccion);

        dto.add(linkTo(methodOn(TransaccionController.class).obtenerTransaccionPorId(idTransaccion)).withSelfRel());
        dto.add(linkTo(methodOn(TransaccionController.class).obtenerTransacciones()).withRel("todas-las-transacciones"));
        dto.add(linkTo(methodOn(TransaccionController.class).obtenerPorCuenta(dto.getIdCuentaOrigen())).withRel("transacciones-de-cuenta-origen"));
        dto.add(linkTo(methodOn(TransaccionController.class).eliminarTransaccion(idTransaccion)).withRel("eliminar"));

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/cuenta/{idCuenta}")
    @Operation(summary = "Lista transacciones donde la cuenta es origen o destino")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerPorCuenta(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(transaccionService.obtenerPorCuenta(idCuenta));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Lista transacciones filtrando por estado (PENDIENTE/COMPLETADA/RECHAZADA/REVERTIDA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Estado invalido")
    })
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerPorEstado(@PathVariable Estado estado) {
        return ResponseEntity.ok(transaccionService.obtenerPorEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Lista transacciones filtrando por tipo (DEPOSITO/RETIRO/TRANSFERENCIA/PAGO)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Tipo invalido")
    })
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerPorTipo(@PathVariable TipoTransaccion tipo) {
        return ResponseEntity.ok(transaccionService.obtenerPorTipo(tipo));
    }

    @GetMapping("/fechas")
    @Operation(summary = "Lista transacciones en un rango de fechas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos o desde > hasta")
    })
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(transaccionService.obtenerPorRangoFechas(desde, hasta));
    }

    @PostMapping
    @Operation(summary = "Crea una transaccion validando cuentas remotas via Feign y aplicando la operacion en ms-cuentas")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaccion completada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos, misma cuenta origen/destino, saldo insuficiente o cuenta no activa"),
            @ApiResponse(responseCode = "503", description = "ms-cuentas no disponible")
    })
    public ResponseEntity<TransaccionResponseDTO> crearTransaccion(
            @Valid @RequestBody TransaccionRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transaccionService.crearTransaccion(requestDTO));
    }

    @PutMapping("/{idTransaccion}")
    @Operation(summary = "Actualiza una transaccion (no permitido si esta COMPLETADA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaccion actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o transaccion COMPLETADA"),
            @ApiResponse(responseCode = "404", description = "Transaccion no encontrada")
    })
    public ResponseEntity<TransaccionResponseDTO> actualizarTransaccion(
            @PathVariable Long idTransaccion,
            @Valid @RequestBody TransaccionRequestDTO dto) {
        return ResponseEntity.ok(transaccionService.actualizarTransaccion(idTransaccion, dto));
    }

    @DeleteMapping("/{idTransaccion}")
    @Operation(summary = "Elimina una transaccion (no permitido si esta COMPLETADA)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transaccion eliminada"),
            @ApiResponse(responseCode = "400", description = "Transaccion COMPLETADA, no se puede eliminar"),
            @ApiResponse(responseCode = "404", description = "Transaccion no encontrada")
    })
    public ResponseEntity<Void> eliminarTransaccion(@PathVariable Long idTransaccion) {
        transaccionService.eliminarTransaccion(idTransaccion);
        return ResponseEntity.noContent().build();
    }
}
