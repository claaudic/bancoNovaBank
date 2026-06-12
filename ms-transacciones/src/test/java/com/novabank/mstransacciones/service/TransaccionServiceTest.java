package com.novabank.mstransacciones.service;

import com.novabank.mstransacciones.client.CuentaFeignClient;
import com.novabank.mstransacciones.dto.request.TransaccionRequestDTO;
import com.novabank.mstransacciones.dto.response.CuentaResponseDTO;
import com.novabank.mstransacciones.dto.response.TransaccionResponseDTO;
import com.novabank.mstransacciones.exception.BusinessRuleException;
import com.novabank.mstransacciones.exception.RemoteServiceException;
import com.novabank.mstransacciones.exception.ResourceNotFoundException;
import com.novabank.mstransacciones.model.Estado;
import com.novabank.mstransacciones.model.TipoTransaccion;
import com.novabank.mstransacciones.model.Transaccion;
import com.novabank.mstransacciones.repository.TransaccionRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private CuentaFeignClient cuentaFeignClient;

    @InjectMocks
    private TransaccionService transaccionService;

    private final Faker faker = new Faker();

    private CuentaResponseDTO cuentaOrigenActiva;
    private CuentaResponseDTO cuentaDestinoActiva;
    private Transaccion transaccionPendiente;
    private TransaccionRequestDTO transferenciaDTO;
    private TransaccionRequestDTO depositoDTO;

    @BeforeEach
    void setUp() {
        cuentaOrigenActiva = new CuentaResponseDTO();
        cuentaOrigenActiva.setIdCuenta(1L);
        cuentaOrigenActiva.setNumeroCuenta(faker.number().digits(8));
        cuentaOrigenActiva.setSaldo(new BigDecimal("500000.00"));
        cuentaOrigenActiva.setRutCliente("11111111-1");
        cuentaOrigenActiva.setEstado("ACTIVA");

        cuentaDestinoActiva = new CuentaResponseDTO();
        cuentaDestinoActiva.setIdCuenta(2L);
        cuentaDestinoActiva.setNumeroCuenta(faker.number().digits(8));
        cuentaDestinoActiva.setSaldo(new BigDecimal("100000.00"));
        cuentaDestinoActiva.setRutCliente("22222222-2");
        cuentaDestinoActiva.setEstado("ACTIVA");

        transaccionPendiente = new Transaccion();
        transaccionPendiente.setIdTransaccion(1L);
        transaccionPendiente.setIdCuentaOrigen(1L);
        transaccionPendiente.setIdCuentaDestino(2L);
        transaccionPendiente.setTipoTransaccion(TipoTransaccion.TRANSFERENCIA);
        transaccionPendiente.setMontoTransaccion(new BigDecimal("50000.00"));
        transaccionPendiente.setFechaTransaccion(LocalDateTime.now());
        transaccionPendiente.setDescripcion(faker.lorem().sentence(3));
        transaccionPendiente.setEstado(Estado.PENDIENTE);

        transferenciaDTO = new TransaccionRequestDTO();
        transferenciaDTO.setIdCuentaOrigen(1L);
        transferenciaDTO.setIdCuentaDestino(2L);
        transferenciaDTO.setTipoTransaccion(TipoTransaccion.TRANSFERENCIA);
        transferenciaDTO.setMontoTransaccion(new BigDecimal("50000.00"));
        transferenciaDTO.setDescripcion(faker.lorem().sentence(3));

        depositoDTO = new TransaccionRequestDTO();
        depositoDTO.setIdCuentaOrigen(1L);
        depositoDTO.setIdCuentaDestino(1L);
        depositoDTO.setTipoTransaccion(TipoTransaccion.DEPOSITO);
        depositoDTO.setMontoTransaccion(new BigDecimal("100000.00"));
        depositoDTO.setDescripcion(faker.lorem().sentence(3));
    }

    @Test
    @DisplayName("crearTransaccion TRANSFERENCIA: cuando todo es valido, completa la transaccion")
    void crearTransaccion_transferenciaValida_completaTransaccion() {
        when(cuentaFeignClient.obtenerCuenta(1L)).thenReturn(cuentaOrigenActiva);
        when(cuentaFeignClient.obtenerCuenta(2L)).thenReturn(cuentaDestinoActiva);
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionPendiente);

        TransaccionResponseDTO resultado = transaccionService.crearTransaccion(transferenciaDTO);

        assertThat(resultado).isNotNull();
        verify(cuentaFeignClient).retirar(eqLong(1L), any());
        verify(cuentaFeignClient).depositar(eqLong(2L), any());
        verify(transaccionRepository, atLeastOnce()).save(any(Transaccion.class));
    }

    @Test
    @DisplayName("crearTransaccion DEPOSITO: cuando todo es valido, completa la operacion")
    void crearTransaccion_depositoValido_completaTransaccion() {
        when(cuentaFeignClient.obtenerCuenta(1L)).thenReturn(cuentaOrigenActiva);
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionPendiente);

        TransaccionResponseDTO resultado = transaccionService.crearTransaccion(depositoDTO);

        assertThat(resultado).isNotNull();
        verify(cuentaFeignClient).depositar(eqLong(1L), any());
        verify(transaccionRepository, atLeastOnce()).save(any(Transaccion.class));
    }

    @Test
    @DisplayName("crearTransaccion: cuando origen y destino son iguales en transferencia, lanza BusinessRuleException")
    void crearTransaccion_mismaCuentaEnTransferencia_lanzaExcepcion() {
        transferenciaDTO.setIdCuentaDestino(1L);

        assertThatThrownBy(() -> transaccionService.crearTransaccion(transferenciaDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("distintas");

        verify(transaccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearTransaccion: cuando la cuenta origen no existe (Feign 404), lanza BusinessRuleException")
    void crearTransaccion_cuentaOrigenInexistente_lanzaBusinessRuleException() {
        when(cuentaFeignClient.obtenerCuenta(anyLong())).thenThrow(feignNotFound());

        assertThatThrownBy(() -> transaccionService.crearTransaccion(transferenciaDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("origen");

        verify(transaccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearTransaccion: cuando ms-cuentas esta caido, lanza RemoteServiceException")
    void crearTransaccion_msCuentasCaido_lanzaRemoteServiceException() {
        when(cuentaFeignClient.obtenerCuenta(anyLong())).thenThrow(feignServerError());

        assertThatThrownBy(() -> transaccionService.crearTransaccion(transferenciaDTO))
                .isInstanceOf(RemoteServiceException.class)
                .hasMessageContaining("ms-cuentas");
    }

    @Test
    @DisplayName("crearTransaccion: cuando la cuenta origen no esta ACTIVA, lanza BusinessRuleException")
    void crearTransaccion_cuentaOrigenInactiva_lanzaExcepcion() {
        cuentaOrigenActiva.setEstado("INACTIVA");
        when(cuentaFeignClient.obtenerCuenta(1L)).thenReturn(cuentaOrigenActiva);

        assertThatThrownBy(() -> transaccionService.crearTransaccion(transferenciaDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("no esta ACTIVA");
    }

    @Test
    @DisplayName("crearTransaccion: cuando el retiro Feign falla, marca la transaccion como RECHAZADA")
    void crearTransaccion_cuandoRetiroRemotoFalla_marcaRechazada() {
        when(cuentaFeignClient.obtenerCuenta(1L)).thenReturn(cuentaOrigenActiva);
        when(cuentaFeignClient.obtenerCuenta(2L)).thenReturn(cuentaDestinoActiva);
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionPendiente);
        when(cuentaFeignClient.retirar(anyLong(), any())).thenThrow(feignBadRequest());

        assertThatThrownBy(() -> transaccionService.crearTransaccion(transferenciaDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("rechazada");

        assertThat(transaccionPendiente.getEstado()).isEqualTo(Estado.RECHAZADA);
    }

    @Test
    @DisplayName("obtenerTransacciones: retorna la lista completa")
    void obtenerTransacciones_retornaLista() {
        when(transaccionRepository.findAll()).thenReturn(List.of(transaccionPendiente));

        List<TransaccionResponseDTO> resultado = transaccionService.obtenerTransacciones();

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("obtenerTransaccionPorId: cuando existe, retorna la transaccion")
    void obtenerTransaccionPorId_cuandoExiste_retorna() {
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccionPendiente));

        TransaccionResponseDTO resultado = transaccionService.obtenerTransaccionPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdTransaccion()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerTransaccionPorId: cuando no existe, lanza ResourceNotFoundException")
    void obtenerTransaccionPorId_cuandoNoExiste_lanzaExcepcion() {
        when(transaccionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transaccionService.obtenerTransaccionPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaccion no encontrada");
    }

    @Test
    @DisplayName("obtenerPorRangoFechas: cuando las fechas son null, lanza BusinessRuleException")
    void obtenerPorRangoFechas_conFechasNull_lanzaExcepcion() {
        assertThatThrownBy(() -> transaccionService.obtenerPorRangoFechas(null, null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("obligatorias");
    }

    @Test
    @DisplayName("obtenerPorRangoFechas: cuando desde es posterior a hasta, lanza BusinessRuleException")
    void obtenerPorRangoFechas_conFechasInvertidas_lanzaExcepcion() {
        LocalDateTime desde = LocalDateTime.now();
        LocalDateTime hasta = desde.minusDays(1);

        assertThatThrownBy(() -> transaccionService.obtenerPorRangoFechas(desde, hasta))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("posterior");
    }

    @Test
    @DisplayName("actualizarTransaccion: cuando esta COMPLETADA, lanza BusinessRuleException")
    void actualizarTransaccion_cuandoCompletada_lanzaExcepcion() {
        transaccionPendiente.setEstado(Estado.COMPLETADA);
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccionPendiente));

        assertThatThrownBy(() -> transaccionService.actualizarTransaccion(1L, transferenciaDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("COMPLETADA");

        verify(transaccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("eliminarTransaccion: cuando esta COMPLETADA, lanza BusinessRuleException")
    void eliminarTransaccion_cuandoCompletada_lanzaExcepcion() {
        transaccionPendiente.setEstado(Estado.COMPLETADA);
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccionPendiente));

        assertThatThrownBy(() -> transaccionService.eliminarTransaccion(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("COMPLETADA");

        verify(transaccionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("eliminarTransaccion: cuando esta PENDIENTE, elimina correctamente")
    void eliminarTransaccion_cuandoPendiente_elimina() {
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccionPendiente));

        transaccionService.eliminarTransaccion(1L);

        verify(transaccionRepository).delete(transaccionPendiente);
    }

    // ----- Helpers -----

    private long eqLong(long value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }

    private FeignException feignNotFound() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://ms-cuentas/api/v1/cuentas/1",
                new HashMap<>(),
                null,
                new RequestTemplate()
        );
        return new FeignException.NotFound(
                "Cuenta no encontrada",
                request,
                "Not Found".getBytes(StandardCharsets.UTF_8),
                new HashMap<>()
        );
    }

    private FeignException feignServerError() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://ms-cuentas/api/v1/cuentas/1",
                new HashMap<>(),
                null,
                new RequestTemplate()
        );
        return new FeignException.InternalServerError(
                "ms-cuentas caido",
                request,
                "Internal Server Error".getBytes(StandardCharsets.UTF_8),
                new HashMap<>()
        );
    }

    private FeignException feignBadRequest() {
        Request request = Request.create(
                Request.HttpMethod.POST,
                "http://ms-cuentas/api/v1/cuentas/1/retirar",
                new HashMap<>(),
                null,
                new RequestTemplate()
        );
        return new FeignException.BadRequest(
                "Saldo insuficiente",
                request,
                "Bad Request".getBytes(StandardCharsets.UTF_8),
                new HashMap<>()
        );
    }
}
