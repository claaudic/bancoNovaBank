package com.novabank.msclientes.service;

import com.novabank.msclientes.dto.request.ClienteRequestDTO;
import com.novabank.msclientes.dto.response.ClienteResponseDTO;
import com.novabank.msclientes.exception.BusinessRuleException;
import com.novabank.msclientes.exception.DuplicateResourceException;
import com.novabank.msclientes.exception.ResourceNotFoundException;
import com.novabank.msclientes.model.Cliente;
import com.novabank.msclientes.model.Estado;
import com.novabank.msclientes.model.Profesion;
import com.novabank.msclientes.repository.ClienteRepository;
import com.novabank.msclientes.repository.ProfesionRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProfesionRepository profesionRepository;

    @InjectMocks
    private ClienteService clienteService;

    private final Faker faker = new Faker();

    private Cliente clienteExistente;
    private Profesion profesionExistente;
    private ClienteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        profesionExistente = new Profesion();
        profesionExistente.setIdProfesion(1L);
        profesionExistente.setNombreProfesion(faker.job().title());

        clienteExistente = new Cliente();
        clienteExistente.setRutCliente("11111111-1");
        clienteExistente.setNumeroSerie(faker.number().digits(9));
        clienteExistente.setNombreCliente(faker.name().firstName());
        clienteExistente.setApellidoCliente(faker.name().lastName());
        clienteExistente.setEmailCliente(faker.internet().emailAddress());
        clienteExistente.setTelefonoCliente("+569" + faker.number().digits(8));
        clienteExistente.setEstado(Estado.ACTIVO);
        clienteExistente.setFechaCreacion(LocalDateTime.now());
        clienteExistente.setProfesion(profesionExistente);

        requestDTO = new ClienteRequestDTO();
        requestDTO.setRutCliente("22222222-2");
        requestDTO.setNumeroSerie(faker.number().digits(9));
        requestDTO.setNombreCliente(faker.name().firstName());
        requestDTO.setApellidoCliente(faker.name().lastName());
        requestDTO.setEmailCliente(faker.internet().emailAddress());
        requestDTO.setTelefonoCliente("+569" + faker.number().digits(8));
        requestDTO.setIdProfesion(1L);
    }

    @Test
    @DisplayName("crearCliente: cuando los datos son validos, debe guardar y retornar el cliente")
    void crearCliente_conDatosValidos_debeGuardarYRetornarCliente() {
        when(clienteRepository.existsById(requestDTO.getRutCliente())).thenReturn(false);
        when(clienteRepository.existsByNumeroSerie(requestDTO.getNumeroSerie())).thenReturn(false);
        when(clienteRepository.existsByEmailCliente(requestDTO.getEmailCliente())).thenReturn(false);
        when(profesionRepository.findById(requestDTO.getIdProfesion())).thenReturn(Optional.of(profesionExistente));

        Cliente clienteGuardado = new Cliente();
        clienteGuardado.setRutCliente(requestDTO.getRutCliente());
        clienteGuardado.setNombreCliente(requestDTO.getNombreCliente());
        clienteGuardado.setApellidoCliente(requestDTO.getApellidoCliente());
        clienteGuardado.setEmailCliente(requestDTO.getEmailCliente());
        clienteGuardado.setEstado(Estado.ACTIVO);
        clienteGuardado.setProfesion(profesionExistente);
        clienteGuardado.setDireccionClientes(List.of());

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        ClienteResponseDTO resultado = clienteService.crearCliente(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getRutCliente()).isEqualTo("22222222-2");
        assertThat(resultado.getNombreCliente()).isEqualTo(requestDTO.getNombreCliente());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("crearCliente: cuando el rut ya existe, debe lanzar DuplicateResourceException")
    void crearCliente_conRutDuplicado_debeLanzarExcepcion() {
        when(clienteRepository.existsById(requestDTO.getRutCliente())).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crearCliente(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("ya existe");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearCliente: cuando el numero de serie ya existe, debe lanzar DuplicateResourceException")
    void crearCliente_conNumeroSerieDuplicado_debeLanzarExcepcion() {
        when(clienteRepository.existsById(requestDTO.getRutCliente())).thenReturn(false);
        when(clienteRepository.existsByNumeroSerie(requestDTO.getNumeroSerie())).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crearCliente(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("numero de serie");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearCliente: cuando la profesion no existe, debe lanzar ResourceNotFoundException")
    void crearCliente_conProfesionInexistente_debeLanzarExcepcion() {
        when(clienteRepository.existsById(requestDTO.getRutCliente())).thenReturn(false);
        when(clienteRepository.existsByNumeroSerie(requestDTO.getNumeroSerie())).thenReturn(false);
        when(clienteRepository.existsByEmailCliente(requestDTO.getEmailCliente())).thenReturn(false);
        when(profesionRepository.findById(requestDTO.getIdProfesion())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.crearCliente(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Profesion");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerClientePorRut: cuando existe, debe retornar el cliente")
    void obtenerClientePorRut_cuandoExiste_debeRetornarCliente() {
        when(clienteRepository.findById("11111111-1")).thenReturn(Optional.of(clienteExistente));

        ClienteResponseDTO resultado = clienteService.obtenerClientePorRut("11111111-1");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getRutCliente()).isEqualTo("11111111-1");
        assertThat(resultado.getNombreCliente()).isEqualTo(clienteExistente.getNombreCliente());
    }

    @Test
    @DisplayName("obtenerClientePorRut: cuando no existe, debe lanzar ResourceNotFoundException")
    void obtenerClientePorRut_cuandoNoExiste_debeLanzarExcepcion() {
        when(clienteRepository.findById("99999999-9")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.obtenerClientePorRut("99999999-9"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    @DisplayName("desactivarCliente: cuando esta ACTIVO, debe cambiar el estado a INACTIVO")
    void desactivarCliente_cuandoEstaActivo_debeDesactivarlo() {
        when(clienteRepository.findById("11111111-1")).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);

        ClienteResponseDTO resultado = clienteService.desactivarCliente("11111111-1");

        assertThat(resultado.getEstado()).isEqualTo(Estado.INACTIVO);
        verify(clienteRepository).save(clienteExistente);
    }

    @Test
    @DisplayName("desactivarCliente: cuando ya esta INACTIVO, debe lanzar BusinessRuleException")
    void desactivarCliente_cuandoYaEstaInactivo_debeLanzarExcepcion() {
        clienteExistente.setEstado(Estado.INACTIVO);
        when(clienteRepository.findById("11111111-1")).thenReturn(Optional.of(clienteExistente));

        assertThatThrownBy(() -> clienteService.desactivarCliente("11111111-1"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ya esta inactivo");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("activarCliente: cuando esta INACTIVO, debe cambiar el estado a ACTIVO")
    void activarCliente_cuandoEstaInactivo_debeActivarlo() {
        clienteExistente.setEstado(Estado.INACTIVO);
        when(clienteRepository.findById("11111111-1")).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);

        ClienteResponseDTO resultado = clienteService.activarCliente("11111111-1");

        assertThat(resultado.getEstado()).isEqualTo(Estado.ACTIVO);
        verify(clienteRepository).save(clienteExistente);
    }

    @Test
    @DisplayName("activarCliente: cuando ya esta ACTIVO, debe lanzar BusinessRuleException")
    void activarCliente_cuandoYaEstaActivo_debeLanzarExcepcion() {
        when(clienteRepository.findById("11111111-1")).thenReturn(Optional.of(clienteExistente));

        assertThatThrownBy(() -> clienteService.activarCliente("11111111-1"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ya esta activo");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerClientes: debe retornar la lista completa convertida a DTOs")
    void obtenerClientes_debeRetornarListaDeDTOs() {
        when(clienteRepository.findAll()).thenReturn(List.of(clienteExistente));

        List<ClienteResponseDTO> resultado = clienteService.obtenerClientes();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRutCliente()).isEqualTo("11111111-1");
    }
}
