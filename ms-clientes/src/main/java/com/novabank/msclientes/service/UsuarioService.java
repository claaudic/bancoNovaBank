package com.novabank.msclientes.service;

import com.novabank.msclientes.dto.request.UsuarioRequestDTO;
import com.novabank.msclientes.dto.response.UsuarioResponseDTO;
import com.novabank.msclientes.exception.BusinessRuleException;
import com.novabank.msclientes.exception.DuplicateResourceException;
import com.novabank.msclientes.exception.ResourceNotFoundException;
import com.novabank.msclientes.model.Usuario;
import com.novabank.msclientes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("El username ya esta registrado");
        }

        Usuario usuario = dto.toEntity();
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setFechaCreacion(LocalDateTime.now());

        Usuario guardado = usuarioRepository.save(usuario);

        log.info("Usuario creado id={} username={} rol={}",
                guardado.getIdUsuario(), guardado.getUsername(), guardado.getRol());

        return UsuarioResponseDTO.toResponseDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return UsuarioResponseDTO.toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerPorRol(String rol) {
        return usuarioRepository.findByRol(rol)
                .stream()
                .map(UsuarioResponseDTO::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean verificarPassword(String username, String passwordPlano) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        boolean coincide = passwordEncoder.matches(passwordPlano, usuario.getPassword());

        log.info("Verificacion de password para username={} resultado={}", username, coincide);

        if (!coincide) {
            throw new BusinessRuleException("Credenciales invalidas");
        }
        return true;
    }

    @Transactional
    public void eliminarUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        log.info("Eliminando usuario id={}", idUsuario);

        usuarioRepository.deleteById(idUsuario);
    }
}
