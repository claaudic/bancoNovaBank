package com.novabank.msclientes.controller;

import com.novabank.msclientes.dto.request.ProfesionRequestDTO;
import com.novabank.msclientes.dto.response.ProfesionResponseDTO;
import com.novabank.msclientes.service.ProfesionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profesiones")
@RequiredArgsConstructor
public class ProfesionController {


    private final ProfesionService profesionService;

    @GetMapping
    public ResponseEntity<List<ProfesionResponseDTO>> obtenerProfesiones() {
        return ResponseEntity.ok(profesionService.obtenerProfesiones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfesionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(profesionService.obtenerPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProfesionResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(profesionService.buscarPorNombre(nombre));
    }

    @PostMapping
    public ResponseEntity<ProfesionResponseDTO> crearProfesion(
            @Valid @RequestBody ProfesionRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profesionService.crearProfesion(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfesionResponseDTO> actualizarProfesion(
            @PathVariable Long id,
            @Valid @RequestBody ProfesionRequestDTO dto) {

        return ResponseEntity.ok(profesionService.actualizarProfesion(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProfesion(@PathVariable Long id) {

        profesionService.eliminarProfesion(id);

        return ResponseEntity.noContent().build();
    }
}
