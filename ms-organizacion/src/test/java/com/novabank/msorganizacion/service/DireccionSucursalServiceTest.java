package com.novabank.msorganizacion.service;

import com.novabank.msorganizacion.dto.request.DireccionSucursalRequestDTO;
import com.novabank.msorganizacion.dto.response.DireccionSucursalResponseDTO;
import com.novabank.msorganizacion.exception.BusinessRuleException;
import com.novabank.msorganizacion.exception.ResourceNotFoundException;
import com.novabank.msorganizacion.model.DireccionSucursal;
import com.novabank.msorganizacion.model.Estado;
import com.novabank.msorganizacion.model.Sucursal;
import com.novabank.msorganizacion.model.TipoDireccion;
import com.novabank.msorganizacion.repository.DireccionSucursalRepository;
import com.novabank.msorganizacion.repository.SucursalRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DireccionSucursalServiceTest {

    @Mock
    private DireccionSucursalRepository direccionRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private DireccionSucursalService direccionService;

    private final Faker faker = new Faker();

    private Sucursal sucursalActiva;
    private DireccionSucursal direccionExistente;
    private DireccionSucursalRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        sucursalActiva = new Sucursal();
        sucursalActiva.setIdSucursal(1L);
        sucursalActiva.setNombre(faker.company().name());
        sucursalActiva.setEstado(Estado.ACTIVO);

        direccionExistente = new DireccionSucursal();
        direccionExistente.setIdDireccion(1L);
        direccionExistente.setTipoDireccion(TipoDireccion.MATRIZ);
        direccionExistente.setCalle(faker.address().streetName());
        direccionExistente.setNumero(faker.address().streetAddressNumber());
        direccionExistente.setDepto("Of 101");
        direccionExistente.setCiudad(faker.address().city());
        direccionExistente.setRegion(faker.address().state());
        direccionExistente.setReferencia(faker.lorem().sentence(3));
        direccionExistente.setSucursal(sucursalActiva);

        requestDTO = new DireccionSucursalRequestDTO();
        requestDTO.setTipoDireccion(TipoDireccion.SUCURSAL);
        requestDTO.setCalle(faker.address().streetName());
        requestDTO.setNumero(faker.address().streetAddressNumber());
        requestDTO.setDepto("Local 5");
        requestDTO.setCiudad(faker.address().city());
        requestDTO.setRegion(faker.address().state());
        requestDTO.setReferencia(faker.lorem().sentence(3));
        requestDTO.setIdSucursal(1L);
    }

    @Test
    @DisplayName("crear: cuando los datos son validos, debe guardar y retornar la direccion")
    void crear_conDatosValidos_debeGuardar() {
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalActiva));
        when(direccionRepository.save(any(DireccionSucursal.class))).thenReturn(direccionExistente);

        DireccionSucursalResponseDTO resultado = direccionService.crear(requestDTO);

        assertThat(resultado).isNotNull();
        verify(direccionRepository).save(any(DireccionSucursal.class));
    }

    @Test
    @DisplayName("crear: cuando la sucursal no existe, lanza ResourceNotFoundException")
    void crear_conSucursalInexistente_lanzaExcepcion() {
        when(sucursalRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> direccionService.crear(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sucursal");

        verify(direccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: cuando la sucursal esta INACTIVA, lanza BusinessRuleException")
    void crear_conSucursalInactiva_lanzaExcepcion() {
        sucursalActiva.setEstado(Estado.INACTIVO);
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalActiva));

        assertThatThrownBy(() -> direccionService.crear(requestDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactiva");

        verify(direccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerPorId: cuando existe, retorna la direccion")
    void obtenerPorId_cuandoExiste_retorna() {
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccionExistente));

        DireccionSucursalResponseDTO resultado = direccionService.obtenerPorId(1L);

        assertThat(resultado.getIdDireccion()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId: cuando no existe, lanza ResourceNotFoundException")
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() {
        when(direccionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> direccionService.obtenerPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Direccion");
    }

    @Test
    @DisplayName("listarPorSucursal: cuando la sucursal no existe, lanza ResourceNotFoundException")
    void listarPorSucursal_sucursalInexistente_lanzaExcepcion() {
        when(sucursalRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> direccionService.listarPorSucursal(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("listarPorCiudad: retorna direcciones por ciudad")
    void listarPorCiudad_retornaLista() {
        when(direccionRepository.findByCiudadIgnoreCase("Santiago"))
                .thenReturn(List.of(direccionExistente));

        List<DireccionSucursalResponseDTO> resultado = direccionService.listarPorCiudad("Santiago");

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("listarPorTipo: retorna direcciones por tipo")
    void listarPorTipo_retornaLista() {
        when(direccionRepository.findByTipoDireccion(TipoDireccion.MATRIZ))
                .thenReturn(List.of(direccionExistente));

        List<DireccionSucursalResponseDTO> resultado =
                direccionService.listarPorTipo(TipoDireccion.MATRIZ);

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("actualizar: cuando la sucursal esta INACTIVA, lanza BusinessRuleException")
    void actualizar_conSucursalInactiva_lanzaExcepcion() {
        sucursalActiva.setEstado(Estado.INACTIVO);
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccionExistente));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalActiva));

        assertThatThrownBy(() -> direccionService.actualizar(1L, requestDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactiva");
    }

    @Test
    @DisplayName("eliminar: cuando existe, elimina correctamente")
    void eliminar_cuandoExiste_elimina() {
        when(direccionRepository.existsById(1L)).thenReturn(true);

        direccionService.eliminar(1L);

        verify(direccionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar: cuando no existe, lanza ResourceNotFoundException")
    void eliminar_cuandoNoExiste_lanzaExcepcion() {
        when(direccionRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> direccionService.eliminar(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
