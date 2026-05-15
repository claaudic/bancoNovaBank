package com.novabank.msclientes.controller;

import com.novabank.msclientes.dto.request.DireccionClienteRequestDTO;
import com.novabank.msclientes.dto.response.DireccionClienteResponseDTO;
import com.novabank.msclientes.model.TipoDireccion;
import com.novabank.msclientes.service.DireccionClienteService;
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
@RequestMapping("/api/v1/direcciones")
@RequiredArgsConstructor
@Tag(name = "Direcciones", description = "Direcciones asociadas a los clientes")
public class DireccionClienteController {

    private final DireccionClienteService direccionClienteService;


    @GetMapping
    @Operation(summary = "Lista todas las direcciones")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<DireccionClienteResponseDTO>> obtenerDirecciones() {
        return ResponseEntity.ok(direccionClienteService.obtenerDirecciones());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una direccion por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Direccion encontrada"),
            @ApiResponse(responseCode = "404", description = "Direccion no encontrada")
    })
    public ResponseEntity<DireccionClienteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(direccionClienteService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{rutCliente}")
    @Operation(summary = "Lista las direcciones de un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<List<DireccionClienteResponseDTO>> obtenerPorRutCliente(
            @PathVariable String rutCliente) {
        return ResponseEntity.ok(direccionClienteService.obtenerPorRutCliente(rutCliente));
    }

    @GetMapping("/ciudad/{ciudad}")
    @Operation(summary = "Lista direcciones por ciudad")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<DireccionClienteResponseDTO>> obtenerPorCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(direccionClienteService.obtenerPorCiudad(ciudad));
    }

    @GetMapping("/tipo/{tipoDireccion}")
    @Operation(summary = "Lista direcciones por tipo (RESIDENCIAL/COMERCIAL)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Tipo invalido")
    })
    public ResponseEntity<List<DireccionClienteResponseDTO>> obtenerPorTipo(
            @PathVariable TipoDireccion tipoDireccion) {
        return ResponseEntity.ok(direccionClienteService.obtenerPorTipo(tipoDireccion));
    }

    @PostMapping("/{rutCliente}")
    @Operation(summary = "Agrega una direccion a un cliente (no permitida si cliente inactivo o direccion duplicada)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Direccion creada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o cliente inactivo"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Direccion duplicada para el cliente")
    })
    public ResponseEntity<DireccionClienteResponseDTO> crearDireccion(
            @PathVariable String rutCliente,
            @Valid @RequestBody DireccionClienteRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(direccionClienteService.crearDireccion(rutCliente, dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza una direccion (no permitida si su cliente esta inactivo)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Direccion actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o cliente inactivo"),
            @ApiResponse(responseCode = "404", description = "Direccion no encontrada")
    })
    public ResponseEntity<DireccionClienteResponseDTO> actualizarDireccion(
            @PathVariable Long id,
            @Valid @RequestBody DireccionClienteRequestDTO dto) {

        return ResponseEntity.ok(direccionClienteService.actualizarDireccion(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una direccion")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Direccion eliminada"),
            @ApiResponse(responseCode = "404", description = "Direccion no encontrada")
    })
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long id) {

        direccionClienteService.eliminarDireccion(id);

        return ResponseEntity.noContent().build();
    }
}
