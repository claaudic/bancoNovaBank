package com.novabank.mscuentas.controller;

import com.novabank.mscuentas.dto.request.TipoCuentaRequestDTO;
import com.novabank.mscuentas.dto.response.TipoCuentaResponseDTO;
import com.novabank.mscuentas.service.TipoCuentaService;
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

@RestController
@RequestMapping("/api/v1/tipos-cuenta")
@RequiredArgsConstructor
@Tag(name = "Tipos de Cuenta", description = "Catalogo de tipos de cuenta (CORRIENTE, AHORRO, VISTA)")
public class TipoCuentaController {

    private final TipoCuentaService tipoCuentaService;

    @GetMapping
    @Operation(summary = "Lista todos los tipos de cuenta")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<TipoCuentaResponseDTO>> obtenerTiposCuenta() {
        return ResponseEntity.ok(tipoCuentaService.obtenerTiposCuenta());
    }

    @GetMapping("/{idTipoCuenta}")
    @Operation(summary = "Obtiene un tipo de cuenta por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "Tipo de cuenta no encontrado")
    })
    public ResponseEntity<TipoCuentaResponseDTO> obtenerPorId(@PathVariable Long idTipoCuenta) {
        return ResponseEntity.ok(tipoCuentaService.obtenerPorId(idTipoCuenta));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busqueda parcial por nombre")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<TipoCuentaResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(tipoCuentaService.buscarPorNombre(nombre));
    }

    @PostMapping
    @Operation(summary = "Crea un tipo de cuenta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo de cuenta creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    public ResponseEntity<TipoCuentaResponseDTO> crearTipoCuenta(@Valid @RequestBody TipoCuentaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoCuentaService.crearTipoCuenta(dto));
    }

    @PutMapping("/{idTipoCuenta}")
    @Operation(summary = "Actualiza un tipo de cuenta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Tipo de cuenta no encontrado"),
            @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    public ResponseEntity<TipoCuentaResponseDTO> actualizarTipoCuenta(
            @PathVariable Long idTipoCuenta,
            @Valid @RequestBody TipoCuentaRequestDTO dto) {
        return ResponseEntity.ok(tipoCuentaService.actualizarTipoCuenta(idTipoCuenta, dto));
    }

    @DeleteMapping("/{idTipoCuenta}")
    @Operation(summary = "Elimina un tipo de cuenta (solo si no tiene cuentas asociadas)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eliminado"),
            @ApiResponse(responseCode = "400", description = "Tiene cuentas asociadas"),
            @ApiResponse(responseCode = "404", description = "Tipo de cuenta no encontrado")
    })
    public ResponseEntity<Void> eliminarTipoCuenta(@PathVariable Long idTipoCuenta) {
        tipoCuentaService.eliminarTipoCuenta(idTipoCuenta);
        return ResponseEntity.noContent().build();
    }
}
