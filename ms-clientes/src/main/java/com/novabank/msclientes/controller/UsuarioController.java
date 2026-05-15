package com.novabank.msclientes.controller;

import com.novabank.msclientes.dto.request.UsuarioRequestDTO;
import com.novabank.msclientes.dto.response.UsuarioResponseDTO;
import com.novabank.msclientes.service.UsuarioService;
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
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro y validacion de usuarios con password cifrada (BCrypt)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crea un usuario con password cifrada en BCrypt")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409", description = "Username duplicado")
    })
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearUsuario(dto));
    }

    @GetMapping
    @Operation(summary = "Lista todos los usuarios (sin exponer password)")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerUsuarios());
    }

    @GetMapping("/{username}")
    @Operation(summary = "Busca un usuario por username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponseDTO> obtenerPorUsername(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.obtenerPorUsername(username));
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Lista usuarios por rol")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(usuarioService.obtenerPorRol(rol));
    }

    @PostMapping("/{username}/verificar")
    @Operation(summary = "Verifica la password en texto plano contra el hash BCrypt almacenado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticacion correcta"),
            @ApiResponse(responseCode = "400", description = "Credenciales invalidas"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Map<String, Boolean>> verificarPassword(
            @PathVariable String username,
            @RequestBody Map<String, String> body) {
        boolean ok = usuarioService.verificarPassword(username, body.get("password"));
        return ResponseEntity.ok(Map.of("autenticado", ok));
    }

    @DeleteMapping("/{idUsuario}")
    @Operation(summary = "Elimina un usuario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
