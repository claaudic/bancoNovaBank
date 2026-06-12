package com.novabank.msorganizacion.service;

import com.novabank.msorganizacion.dto.request.SucursalRequestDTO;
import com.novabank.msorganizacion.dto.response.SucursalResponseDTO;
import com.novabank.msorganizacion.exception.BusinessRuleException;
import com.novabank.msorganizacion.exception.DuplicateResourceException;
import com.novabank.msorganizacion.exception.ResourceNotFoundException;
import com.novabank.msorganizacion.model.Estado;
import com.novabank.msorganizacion.model.Sucursal;
import com.novabank.msorganizacion.repository.DireccionSucursalRepository;
import com.novabank.msorganizacion.repository.EjecutivoRepository;
import com.novabank.msorganizacion.repository.SucursalRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private EjecutivoRepository ejecutivoRepository;

    @Mock
    private DireccionSucursalRepository direccionRepository;

    @InjectMocks
    private SucursalService sucursalService;

    private final Faker faker = new Faker();

    private Sucursal sucursalActiva;
    private SucursalRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        sucursalActiva = new Sucursal();
        sucursalActiva.setIdSucursal(1L);
        sucursalActiva.setNombre(faker.company().name());
        sucursalActiva.setEmail(faker.internet().emailAddress());
        sucursalActiva.setTelefono("+569" + faker.number().digits(8));
        sucursalActiva.setEstado(Estado.ACTIVO);
        sucursalActiva.setFechaCreacion(LocalDateTime.now());

        requestDTO = new SucursalRequestDTO();
        requestDTO.setNombre(faker.company().name());
        requestDTO.setEmail(faker.internet().emailAddress());
        requestDTO.setTelefono("+569" + faker.number().digits(8));
    }

    @Test
    @DisplayName("crear: cuando los datos son validos, debe guardar y retornar la sucursal")
    void crear_conDatosValidos_debeGuardar() {
        when(sucursalRepository.existsByNombre(anyString())).thenReturn(false);
        when(sucursalRepository.existsByEmail(anyString())).thenReturn(false);
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(sucursalActiva);

        SucursalResponseDTO resultado = sucursalService.crear(requestDTO);

        assertThat(resultado).isNotNull();
        verify(sucursalRepository).save(any(Sucursal.class));
    }

    @Test
    @DisplayName("crear: cuando el nombre ya existe, lanza DuplicateResourceException")
    void crear_conNombreDuplicado_lanzaExcepcion() {
        when(sucursalRepository.existsByNombre(anyString())).thenReturn(true);

        assertThatThrownBy(() -> sucursalService.crear(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("nombre");

        verify(sucursalRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: cuando el email ya existe, lanza DuplicateResourceException")
    void crear_conEmailDuplicado_lanzaExcepcion() {
        when(sucursalRepository.existsByNombre(anyString())).thenReturn(false);
        when(sucursalRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> sucursalService.crear(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");
    }

    @Test
    @DisplayName("obtenerPorId: cuando existe, retorna la sucursal")
    void obtenerPorId_cuandoExiste_retorna() {
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalActiva));

        SucursalResponseDTO resultado = sucursalService.obtenerPorId(1L);

        assertThat(resultado.getIdSucursal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId: cuando no existe, lanza ResourceNotFoundException")
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() {
        when(sucursalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sucursalService.obtenerPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sucursal no encontrada");
    }

    @Test
    @DisplayName("listarTodas: retorna la lista completa")
    void listarTodas_retornaLista() {
        when(sucursalRepository.findAll()).thenReturn(List.of(sucursalActiva));

        List<SucursalResponseDTO> resultado = sucursalService.listarTodas();

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("actualizar: cuando esta INACTIVA, lanza BusinessRuleException")
    void actualizar_cuandoInactiva_lanzaExcepcion() {
        sucursalActiva.setEstado(Estado.INACTIVO);
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalActiva));

        assertThatThrownBy(() -> sucursalService.actualizar(1L, requestDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactiva");

        verify(sucursalRepository, never()).save(any());
    }

    @Test
    @DisplayName("activar: cuando ya esta ACTIVA, lanza BusinessRuleException")
    void activar_cuandoYaActiva_lanzaExcepcion() {
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalActiva));

        assertThatThrownBy(() -> sucursalService.activar(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ya esta activa");
    }

    @Test
    @DisplayName("desactivar: cuando esta ACTIVA, cambia el estado a INACTIVO")
    void desactivar_cuandoActiva_laDesactiva() {
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalActiva));
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(sucursalActiva);

        SucursalResponseDTO resultado = sucursalService.desactivar(1L);

        assertThat(resultado.getEstado()).isEqualTo(Estado.INACTIVO);
    }

    @Test
    @DisplayName("eliminar: cuando no tiene ejecutivos ni direcciones, elimina correctamente")
    void eliminar_sinAsociados_elimina() {
        when(sucursalRepository.existsById(1L)).thenReturn(true);
        when(ejecutivoRepository.existsBySucursalIdSucursal(1L)).thenReturn(false);
        when(direccionRepository.existsBySucursalIdSucursal(1L)).thenReturn(false);

        sucursalService.eliminar(1L);

        verify(sucursalRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar: cuando tiene ejecutivos asociados, lanza BusinessRuleException")
    void eliminar_conEjecutivosAsociados_lanzaExcepcion() {
        when(sucursalRepository.existsById(1L)).thenReturn(true);
        when(ejecutivoRepository.existsBySucursalIdSucursal(1L)).thenReturn(true);

        assertThatThrownBy(() -> sucursalService.eliminar(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ejecutivos");

        verify(sucursalRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("eliminar: cuando tiene direcciones asociadas, lanza BusinessRuleException")
    void eliminar_conDireccionesAsociadas_lanzaExcepcion() {
        when(sucursalRepository.existsById(1L)).thenReturn(true);
        when(ejecutivoRepository.existsBySucursalIdSucursal(1L)).thenReturn(false);
        when(direccionRepository.existsBySucursalIdSucursal(1L)).thenReturn(true);

        assertThatThrownBy(() -> sucursalService.eliminar(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("direcciones");
    }
}
