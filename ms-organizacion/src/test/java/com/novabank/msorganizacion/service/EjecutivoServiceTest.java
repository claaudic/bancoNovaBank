package com.novabank.msorganizacion.service;

import com.novabank.msorganizacion.dto.request.EjecutivoRequestDTO;
import com.novabank.msorganizacion.dto.response.EjecutivoResponseDTO;
import com.novabank.msorganizacion.exception.BusinessRuleException;
import com.novabank.msorganizacion.exception.DuplicateResourceException;
import com.novabank.msorganizacion.exception.ResourceNotFoundException;
import com.novabank.msorganizacion.model.Ejecutivo;
import com.novabank.msorganizacion.model.Estado;
import com.novabank.msorganizacion.model.Sucursal;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EjecutivoServiceTest {

    @Mock
    private EjecutivoRepository ejecutivoRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private EjecutivoService ejecutivoService;

    private final Faker faker = new Faker();

    private Sucursal sucursalActiva;
    private Ejecutivo ejecutivoActivo;
    private EjecutivoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        sucursalActiva = new Sucursal();
        sucursalActiva.setIdSucursal(1L);
        sucursalActiva.setNombre(faker.company().name());
        sucursalActiva.setEstado(Estado.ACTIVO);

        ejecutivoActivo = new Ejecutivo();
        ejecutivoActivo.setIdEjecutivo(1L);
        ejecutivoActivo.setNombre(faker.name().firstName());
        ejecutivoActivo.setApellido(faker.name().lastName());
        ejecutivoActivo.setEmail(faker.internet().emailAddress());
        ejecutivoActivo.setTelefono("+569" + faker.number().digits(8));
        ejecutivoActivo.setCargo("Asesor financiero");
        ejecutivoActivo.setEstado(Estado.ACTIVO);
        ejecutivoActivo.setFechaIngreso(LocalDate.now());
        ejecutivoActivo.setSucursal(sucursalActiva);

        requestDTO = new EjecutivoRequestDTO();
        requestDTO.setNombre(faker.name().firstName());
        requestDTO.setApellido(faker.name().lastName());
        requestDTO.setEmail(faker.internet().emailAddress());
        requestDTO.setTelefono("+569" + faker.number().digits(8));
        requestDTO.setCargo("Ejecutivo de cuentas");
        requestDTO.setIdSucursal(1L);
    }

    @Test
    @DisplayName("crear: cuando los datos son validos, debe guardar y retornar el ejecutivo")
    void crear_conDatosValidos_debeGuardar() {
        when(ejecutivoRepository.existsByEmail(anyString())).thenReturn(false);
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalActiva));
        when(ejecutivoRepository.save(any(Ejecutivo.class))).thenReturn(ejecutivoActivo);

        EjecutivoResponseDTO resultado = ejecutivoService.crear(requestDTO);

        assertThat(resultado).isNotNull();
        verify(ejecutivoRepository).save(any(Ejecutivo.class));
    }

    @Test
    @DisplayName("crear: cuando el email ya existe, lanza DuplicateResourceException")
    void crear_conEmailDuplicado_lanzaExcepcion() {
        when(ejecutivoRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> ejecutivoService.crear(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");

        verify(ejecutivoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: cuando la sucursal no existe, lanza ResourceNotFoundException")
    void crear_conSucursalInexistente_lanzaExcepcion() {
        when(ejecutivoRepository.existsByEmail(anyString())).thenReturn(false);
        when(sucursalRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ejecutivoService.crear(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sucursal");
    }

    @Test
    @DisplayName("crear: cuando la sucursal esta INACTIVA, lanza BusinessRuleException")
    void crear_conSucursalInactiva_lanzaExcepcion() {
        sucursalActiva.setEstado(Estado.INACTIVO);
        when(ejecutivoRepository.existsByEmail(anyString())).thenReturn(false);
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalActiva));

        assertThatThrownBy(() -> ejecutivoService.crear(requestDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactiva");
    }

    @Test
    @DisplayName("obtenerPorId: cuando existe, retorna el ejecutivo")
    void obtenerPorId_cuandoExiste_retorna() {
        when(ejecutivoRepository.findById(1L)).thenReturn(Optional.of(ejecutivoActivo));

        EjecutivoResponseDTO resultado = ejecutivoService.obtenerPorId(1L);

        assertThat(resultado.getIdEjecutivo()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId: cuando no existe, lanza ResourceNotFoundException")
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() {
        when(ejecutivoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ejecutivoService.obtenerPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ejecutivo no encontrado");
    }

    @Test
    @DisplayName("listarPorSucursal: cuando la sucursal no existe, lanza ResourceNotFoundException")
    void listarPorSucursal_sucursalInexistente_lanzaExcepcion() {
        when(sucursalRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> ejecutivoService.listarPorSucursal(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("actualizar: cuando esta INACTIVO, lanza BusinessRuleException")
    void actualizar_cuandoInactivo_lanzaExcepcion() {
        ejecutivoActivo.setEstado(Estado.INACTIVO);
        when(ejecutivoRepository.findById(1L)).thenReturn(Optional.of(ejecutivoActivo));

        assertThatThrownBy(() -> ejecutivoService.actualizar(1L, requestDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactivo");

        verify(ejecutivoRepository, never()).save(any());
    }

    @Test
    @DisplayName("activar: cuando ya esta ACTIVO, lanza BusinessRuleException")
    void activar_cuandoYaActivo_lanzaExcepcion() {
        when(ejecutivoRepository.findById(1L)).thenReturn(Optional.of(ejecutivoActivo));

        assertThatThrownBy(() -> ejecutivoService.activar(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ya esta activo");
    }

    @Test
    @DisplayName("desactivar: cuando esta ACTIVO, cambia el estado a INACTIVO")
    void desactivar_cuandoActivo_loDesactiva() {
        when(ejecutivoRepository.findById(1L)).thenReturn(Optional.of(ejecutivoActivo));
        when(ejecutivoRepository.save(any(Ejecutivo.class))).thenReturn(ejecutivoActivo);

        EjecutivoResponseDTO resultado = ejecutivoService.desactivar(1L);

        assertThat(resultado.getEstado()).isEqualTo(Estado.INACTIVO);
    }

    @Test
    @DisplayName("eliminar: cuando existe, elimina correctamente")
    void eliminar_cuandoExiste_elimina() {
        when(ejecutivoRepository.existsById(1L)).thenReturn(true);

        ejecutivoService.eliminar(1L);

        verify(ejecutivoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar: cuando no existe, lanza ResourceNotFoundException")
    void eliminar_cuandoNoExiste_lanzaExcepcion() {
        when(ejecutivoRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> ejecutivoService.eliminar(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
