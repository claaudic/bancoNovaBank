package com.novabank.msorganizacion.controller;

import com.novabank.msorganizacion.dto.request.SucursalRequestDTO;
import com.novabank.msorganizacion.dto.response.SucursalResponseDTO;
import com.novabank.msorganizacion.service.SucursalService;
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
@RequestMapping("/api/v1/sucursales")
@RequiredArgsConstructor
@Tag(name = "Sucursales", description = "Gestion de sucursales del banco")

public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    @Operation(summary = "Lista todas las sucursales")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<SucursalResponseDTO>> listar() {
        return ResponseEntity.ok(sucursalService.listarTodas());
    }

    @GetMapping("/{idSucursal}")
    @Operation(summary = "Obtiene una sucursal por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucursal encontrada"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    public ResponseEntity<SucursalResponseDTO> obtener(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(sucursalService.obtenerPorId(idSucursal));
    }

    @GetMapping("/activas")
    @Operation(summary = "Lista solo las sucursales ACTIVAS")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<SucursalResponseDTO>> listarActivas() {
        return ResponseEntity.ok(sucursalService.listarActivas());
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busqueda parcial por nombre")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<SucursalResponseDTO>> buscar(@RequestParam String nombre) {
        return ResponseEntity.ok(sucursalService.buscarPorNombre(nombre));
    }

    @PostMapping
    @Operation(summary = "Crea una sucursal (estado ACTIVO y fechaCreacion automaticos)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sucursal creada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409", description = "Nombre o email duplicado")
    })
    public ResponseEntity<SucursalResponseDTO> crear(@Valid @RequestBody SucursalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalService.crear(dto));
    }

    @PutMapping("/{idSucursal}")
    @Operation(summary = "Actualiza una sucursal (no permitido si esta INACTIVA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucursal actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o sucursal inactiva"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada"),
            @ApiResponse(responseCode = "409", description = "Nombre o email duplicado")
    })
    public ResponseEntity<SucursalResponseDTO> actualizar(
            @PathVariable Long idSucursal,
            @Valid @RequestBody SucursalRequestDTO dto) {
        return ResponseEntity.ok(sucursalService.actualizar(idSucursal, dto));
    }

    @PatchMapping("/{idSucursal}/activar")
    @Operation(summary = "Activa una sucursal inactiva")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucursal activada"),
            @ApiResponse(responseCode = "400", description = "La sucursal ya esta activa"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    public ResponseEntity<SucursalResponseDTO> activar(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(sucursalService.activar(idSucursal));
    }

    @PatchMapping("/{idSucursal}/desactivar")
    @Operation(summary = "Desactiva una sucursal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucursal desactivada"),
            @ApiResponse(responseCode = "400", description = "La sucursal ya esta inactiva"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    public ResponseEntity<SucursalResponseDTO> desactivar(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(sucursalService.desactivar(idSucursal));
    }

    @DeleteMapping("/{idSucursal}")
    @Operation(summary = "Elimina una sucursal (solo si no tiene ejecutivos ni direcciones)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sucursal eliminada"),
            @ApiResponse(responseCode = "400", description = "Tiene ejecutivos o direcciones asociadas"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long idSucursal) {
        sucursalService.eliminar(idSucursal);
        return ResponseEntity.noContent().build();
    }
}
