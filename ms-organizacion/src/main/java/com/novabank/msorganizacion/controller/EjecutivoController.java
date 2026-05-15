package com.novabank.msorganizacion.controller;

import com.novabank.msorganizacion.dto.request.EjecutivoRequestDTO;
import com.novabank.msorganizacion.dto.response.EjecutivoResponseDTO;
import com.novabank.msorganizacion.service.EjecutivoService;
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
@RequestMapping("/api/v1/ejecutivos")
@RequiredArgsConstructor
@Tag(name = "Ejecutivos", description = "Gestion de ejecutivos asignados a sucursales")
public class EjecutivoController {

    private final EjecutivoService ejecutivoService;

    @GetMapping
    @Operation(summary = "Lista todos los ejecutivos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<EjecutivoResponseDTO>> listar() {
        return ResponseEntity.ok(ejecutivoService.listarTodos());
    }

    @GetMapping("/{idEjecutivo}")
    @Operation(summary = "Obtiene un ejecutivo por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejecutivo encontrado"),
            @ApiResponse(responseCode = "404", description = "Ejecutivo no encontrado")
    })
    public ResponseEntity<EjecutivoResponseDTO> obtener(@PathVariable Long idEjecutivo) {
        return ResponseEntity.ok(ejecutivoService.obtenerPorId(idEjecutivo));
    }

    @GetMapping("/sucursal/{idSucursal}")
    @Operation(summary = "Lista ejecutivos de una sucursal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "404", description = "Sucursal no existe")
    })
    public ResponseEntity<List<EjecutivoResponseDTO>> listarPorSucursal(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(ejecutivoService.listarPorSucursal(idSucursal));
    }

    @GetMapping("/sucursal/{idSucursal}/activos")
    @Operation(summary = "Lista solo los ejecutivos ACTIVOS de una sucursal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "404", description = "Sucursal no existe")
    })
    public ResponseEntity<List<EjecutivoResponseDTO>> listarActivosPorSucursal(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(ejecutivoService.listarActivosPorSucursal(idSucursal));
    }

    @PostMapping
    @Operation(summary = "Crea un ejecutivo (estado ACTIVO y fechaIngreso automaticos)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ejecutivo creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o sucursal inactiva"),
            @ApiResponse(responseCode = "404", description = "Sucursal no existe"),
            @ApiResponse(responseCode = "409", description = "Email duplicado")
    })
    public ResponseEntity<EjecutivoResponseDTO> crear(@Valid @RequestBody EjecutivoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ejecutivoService.crear(dto));
    }

    @PutMapping("/{idEjecutivo}")
    @Operation(summary = "Actualiza un ejecutivo (no permitido si esta INACTIVO)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejecutivo actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o ejecutivo inactivo"),
            @ApiResponse(responseCode = "404", description = "Ejecutivo o sucursal no encontrado"),
            @ApiResponse(responseCode = "409", description = "Email duplicado")
    })
    public ResponseEntity<EjecutivoResponseDTO> actualizar(
            @PathVariable Long idEjecutivo,
            @Valid @RequestBody EjecutivoRequestDTO dto) {
        return ResponseEntity.ok(ejecutivoService.actualizar(idEjecutivo, dto));
    }

    @PatchMapping("/{idEjecutivo}/activar")
    @Operation(summary = "Activa un ejecutivo inactivo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejecutivo activado"),
            @ApiResponse(responseCode = "400", description = "El ejecutivo ya esta activo"),
            @ApiResponse(responseCode = "404", description = "Ejecutivo no encontrado")
    })
    public ResponseEntity<EjecutivoResponseDTO> activar(@PathVariable Long idEjecutivo) {
        return ResponseEntity.ok(ejecutivoService.activar(idEjecutivo));
    }

    @PatchMapping("/{idEjecutivo}/desactivar")
    @Operation(summary = "Desactiva un ejecutivo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejecutivo desactivado"),
            @ApiResponse(responseCode = "400", description = "El ejecutivo ya esta inactivo"),
            @ApiResponse(responseCode = "404", description = "Ejecutivo no encontrado")
    })
    public ResponseEntity<EjecutivoResponseDTO> desactivar(@PathVariable Long idEjecutivo) {
        return ResponseEntity.ok(ejecutivoService.desactivar(idEjecutivo));
    }

    @DeleteMapping("/{idEjecutivo}")
    @Operation(summary = "Elimina un ejecutivo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ejecutivo eliminado"),
            @ApiResponse(responseCode = "404", description = "Ejecutivo no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long idEjecutivo) {
        ejecutivoService.eliminar(idEjecutivo);
        return ResponseEntity.noContent().build();
    }
}
