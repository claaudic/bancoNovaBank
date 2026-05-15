package com.novabank.msorganizacion.controller;

import com.novabank.msorganizacion.dto.request.DireccionSucursalRequestDTO;
import com.novabank.msorganizacion.dto.response.DireccionSucursalResponseDTO;
import com.novabank.msorganizacion.model.TipoDireccion;
import com.novabank.msorganizacion.service.DireccionSucursalService;
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
@RequestMapping("/api/v1/direcciones-sucursal")
@RequiredArgsConstructor
@Tag(name = "Direcciones de Sucursal", description = "Direcciones fisicas de cada sucursal")
public class DireccionSucursalController {

    private final DireccionSucursalService direccionService;

    @GetMapping
    @Operation(summary = "Lista todas las direcciones")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<DireccionSucursalResponseDTO>> listar() {
        return ResponseEntity.ok(direccionService.listarTodas());
    }

    @GetMapping("/{idDireccion}")
    @Operation(summary = "Obtiene una direccion por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Direccion encontrada"),
            @ApiResponse(responseCode = "404", description = "Direccion no encontrada")
    })
    public ResponseEntity<DireccionSucursalResponseDTO> obtener(@PathVariable Long idDireccion) {
        return ResponseEntity.ok(direccionService.obtenerPorId(idDireccion));
    }

    @GetMapping("/sucursal/{idSucursal}")
    @Operation(summary = "Lista direcciones de una sucursal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "404", description = "Sucursal no existe")
    })
    public ResponseEntity<List<DireccionSucursalResponseDTO>> listarPorSucursal(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(direccionService.listarPorSucursal(idSucursal));
    }

    @GetMapping("/ciudad/{ciudad}")
    @Operation(summary = "Lista direcciones por ciudad")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<DireccionSucursalResponseDTO>> listarPorCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(direccionService.listarPorCiudad(ciudad));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Lista direcciones por tipo (MATRIZ, SUCURSAL, OFICINA, BODEGA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Tipo invalido")
    })
    public ResponseEntity<List<DireccionSucursalResponseDTO>> listarPorTipo(@PathVariable TipoDireccion tipo) {
        return ResponseEntity.ok(direccionService.listarPorTipo(tipo));
    }

    @PostMapping
    @Operation(summary = "Crea una direccion para una sucursal")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Direccion creada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o sucursal inactiva"),
            @ApiResponse(responseCode = "404", description = "Sucursal no existe")
    })
    public ResponseEntity<DireccionSucursalResponseDTO> crear(@Valid @RequestBody DireccionSucursalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(direccionService.crear(dto));
    }

    @PutMapping("/{idDireccion}")
    @Operation(summary = "Actualiza una direccion")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Direccion actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o sucursal inactiva"),
            @ApiResponse(responseCode = "404", description = "Direccion o sucursal no encontrada")
    })
    public ResponseEntity<DireccionSucursalResponseDTO> actualizar(
            @PathVariable Long idDireccion,
            @Valid @RequestBody DireccionSucursalRequestDTO dto) {
        return ResponseEntity.ok(direccionService.actualizar(idDireccion, dto));
    }

    @DeleteMapping("/{idDireccion}")
    @Operation(summary = "Elimina una direccion")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Direccion eliminada"),
            @ApiResponse(responseCode = "404", description = "Direccion no encontrada")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long idDireccion) {
        direccionService.eliminar(idDireccion);
        return ResponseEntity.noContent().build();
    }
}
