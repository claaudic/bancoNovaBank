package com.novabank.msclientes.controller;

import com.novabank.msclientes.dto.request.ClienteRequestDTO;
import com.novabank.msclientes.dto.response.ClienteResponseDTO;
import com.novabank.msclientes.model.Estado;
import com.novabank.msclientes.service.ClienteService;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestion de clientes del banco")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Lista todos los clientes")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<ClienteResponseDTO>> obtenerClientes() {
        return ResponseEntity.ok(clienteService.obtenerClientes());
    }

    @GetMapping("/{rutCliente}")
    @Operation(summary = "Obtiene un cliente por su rut con enlaces HATEOAS")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ClienteResponseDTO> obtenerCliente(@PathVariable String rutCliente) {
        ClienteResponseDTO dto = clienteService.obtenerClientePorRut(rutCliente);

        dto.add(linkTo(methodOn(ClienteController.class).obtenerCliente(rutCliente)).withSelfRel());
        dto.add(linkTo(methodOn(ClienteController.class).obtenerClientes()).withRel("todos-los-clientes"));
        dto.add(linkTo(methodOn(ClienteController.class).activar(rutCliente)).withRel("activar"));
        dto.add(linkTo(methodOn(ClienteController.class).desactivar(rutCliente)).withRel("desactivar"));
        dto.add(linkTo(methodOn(ClienteController.class).eliminarCliente(rutCliente)).withRel("eliminar"));

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Lista clientes filtrando por estado (ACTIVO/INACTIVO)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Estado invalido")
    })
    public ResponseEntity<List<ClienteResponseDTO>> obtenerPorEstado(@PathVariable Estado estado) {
        return ResponseEntity.ok(clienteService.obtenerPorEstado(estado));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busqueda parcial por nombre o apellido")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<ClienteResponseDTO>> buscarPorNombre(@RequestParam String texto) {
        return ResponseEntity.ok(clienteService.buscarPorNombre(texto));
    }

    @GetMapping("/profesion/{idProfesion}")
    @Operation(summary = "Lista clientes asociados a una profesion")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "404", description = "Profesion no existe")
    })
    public ResponseEntity<List<ClienteResponseDTO>> obtenerPorProfesion(@PathVariable Long idProfesion) {
        return ResponseEntity.ok(clienteService.obtenerPorProfesion(idProfesion));
    }

    @PostMapping
    @Operation(summary = "Crea un cliente con sus direcciones iniciales")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Profesion no existe"),
            @ApiResponse(responseCode = "409", description = "Cliente, numeroSerie o email duplicado")
    })
    public ResponseEntity<ClienteResponseDTO> crearCliente(
            @Valid @RequestBody ClienteRequestDTO dtoCliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearCliente(dtoCliente));
    }

    @PutMapping("/{rutCliente}")
    @Operation(summary = "Actualiza datos del cliente (no permitido si esta inactivo)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o cliente inactivo"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Email duplicado")
    })
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
            @PathVariable String rutCliente,
            @Valid @RequestBody ClienteRequestDTO dtoCliente) {
        return ResponseEntity.ok(clienteService.actualizarCliente(rutCliente, dtoCliente));
    }

    @PatchMapping("/{rutCliente}/activar")
    @Operation(summary = "Reactiva un cliente inactivo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente activado"),
            @ApiResponse(responseCode = "400", description = "El cliente ya esta activo"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ClienteResponseDTO> activar(@PathVariable String rutCliente) {
        return ResponseEntity.ok(clienteService.activarCliente(rutCliente));
    }

    @PatchMapping("/{rutCliente}/desactivar")
    @Operation(summary = "Desactiva un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente desactivado"),
            @ApiResponse(responseCode = "400", description = "El cliente ya esta inactivo"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ClienteResponseDTO> desactivar(@PathVariable String rutCliente) {
        return ResponseEntity.ok(clienteService.desactivarCliente(rutCliente));
    }

    @DeleteMapping("/{rutCliente}")
    @Operation(summary = "Elimina un cliente y sus direcciones")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<Void> eliminarCliente(@PathVariable String rutCliente) {
        clienteService.eliminarCliente(rutCliente);
        return ResponseEntity.noContent().build();
    }
}
