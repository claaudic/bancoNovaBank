package com.novabank.msclientes.controller;

import com.novabank.msclientes.dto.request.ProfesionRequestDTO;
import com.novabank.msclientes.dto.response.ProfesionResponseDTO;
import com.novabank.msclientes.service.ProfesionService;
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
@RequestMapping("/api/v1/profesiones")
@RequiredArgsConstructor
@Tag(name = "Profesiones", description = "Catalogo de profesiones asociadas a los clientes")
public class ProfesionController {


    private final ProfesionService profesionService;

    @GetMapping
    @Operation(summary = "Lista todas las profesiones")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<ProfesionResponseDTO>> obtenerProfesiones() {
        return ResponseEntity.ok(profesionService.obtenerProfesiones());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una profesion por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profesion encontrada"),
            @ApiResponse(responseCode = "404", description = "Profesion no encontrada")
    })
    public ResponseEntity<ProfesionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(profesionService.obtenerPorId(id));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busqueda parcial por nombre")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<ProfesionResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(profesionService.buscarPorNombre(nombre));
    }

    @PostMapping
    @Operation(summary = "Crea una profesion")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profesion creada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409", description = "Profesion duplicada")
    })
    public ResponseEntity<ProfesionResponseDTO> crearProfesion(
            @Valid @RequestBody ProfesionRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profesionService.crearProfesion(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza una profesion")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profesion actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Profesion no encontrada"),
            @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    public ResponseEntity<ProfesionResponseDTO> actualizarProfesion(
            @PathVariable Long id,
            @Valid @RequestBody ProfesionRequestDTO dto) {

        return ResponseEntity.ok(profesionService.actualizarProfesion(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una profesion (solo si no tiene clientes asociados)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profesion eliminada"),
            @ApiResponse(responseCode = "400", description = "Tiene clientes asociados"),
            @ApiResponse(responseCode = "404", description = "Profesion no encontrada")
    })
    public ResponseEntity<Void> eliminarProfesion(@PathVariable Long id) {

        profesionService.eliminarProfesion(id);

        return ResponseEntity.noContent().build();
    }
}
