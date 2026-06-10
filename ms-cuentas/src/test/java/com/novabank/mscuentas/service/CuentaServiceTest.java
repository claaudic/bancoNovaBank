package com.novabank.mscuentas.service;

import com.novabank.mscuentas.client.ClienteFeignClient;
import com.novabank.mscuentas.dto.request.CuentaRequestDTO;
import com.novabank.mscuentas.dto.request.MontoRequestDTO;
import com.novabank.mscuentas.dto.request.TransferenciaRequestDTO;
import com.novabank.mscuentas.dto.response.CuentaResponseDTO;
import com.novabank.mscuentas.dto.response.TransferenciaResponseDTO;
import com.novabank.mscuentas.exception.BusinessRuleException;
import com.novabank.mscuentas.exception.DuplicateResourceException;
import com.novabank.mscuentas.exception.RemoteServiceException;
import com.novabank.mscuentas.exception.ResourceNotFoundException;
import com.novabank.mscuentas.model.Cuenta;
import com.novabank.mscuentas.model.EstadoCuenta;
import com.novabank.mscuentas.model.TipoCuenta;
import com.novabank.mscuentas.repository.CuentaRepository;
import com.novabank.mscuentas.repository.TipoCuentaRepository;
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
import java.time.LocalDate;
import java.util.HashMap;
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
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private TipoCuentaRepository tipoCuentaRepository;

    @Mock
    private ClienteFeignClient clienteFeignClient;

    @InjectMocks
    private CuentaService cuentaService;

    private final Faker faker = new Faker();

    private Cuenta cuentaActiva;
    private TipoCuenta tipoCuenta;
    private CuentaRequestDTO requestDTO;
    private MontoRequestDTO montoDTO;

    @BeforeEach
    void setUp() {
        tipoCuenta = new TipoCuenta();
        tipoCuenta.setIdTipoCuenta(1L);
        tipoCuenta.setNombreTipoCuenta("CORRIENTE");

        cuentaActiva = new Cuenta();
        cuentaActiva.setIdCuenta(1L);
        cuentaActiva.setNumeroCuenta(faker.number().digits(8));
        cuentaActiva.setSaldo(new BigDecimal("500000.00"));
        cuentaActiva.setRutCliente("11111111-1");
        cuentaActiva.setEstado(EstadoCuenta.ACTIVA);
        cuentaActiva.setFechaCreacion(LocalDate.now());
        cuentaActiva.setTipoCuenta(tipoCuenta);

        requestDTO = new CuentaRequestDTO();
        requestDTO.setNumeroCuenta(faker.number().digits(8));
        requestDTO.setSaldo(new BigDecimal("100000.00"));
        requestDTO.setRutCliente("11111111-1");
        requestDTO.setIdTipoCuenta(1L);

        montoDTO = new MontoRequestDTO();
        montoDTO.setMonto(new BigDecimal("50000.00"));
    }

    @Test
    @DisplayName("crearCuenta: cuando los datos son validos, debe guardar y retornar la cuenta")
    void crearCuenta_conDatosValidos_debeGuardarYRetornar() {
        when(cuentaRepository.existsByNumeroCuenta(requestDTO.getNumeroCuenta())).thenReturn(false);
        when(tipoCuentaRepository.findById(1L)).thenReturn(Optional.of(tipoCuenta));
        when(clienteFeignClient.obtenerCliente("11111111-1")).thenReturn(null);
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaActiva);

        CuentaResponseDTO resultado = cuentaService.crearCuenta(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getRutCliente()).isEqualTo("11111111-1");
        verify(cuentaRepository).save(any(Cuenta.class));
    }

    @Test
    @DisplayName("crearCuenta: cuando el numero de cuenta ya existe, lanza DuplicateResourceException")
    void crearCuenta_conNumeroDuplicado_lanzaExcepcion() {
        when(cuentaRepository.existsByNumeroCuenta(anyString())).thenReturn(true);

        assertThatThrownBy(() -> cuentaService.crearCuenta(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("ya existe");

        verify(cuentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearCuenta: cuando el tipo de cuenta no existe, lanza ResourceNotFoundException")
    void crearCuenta_conTipoCuentaInexistente_lanzaExcepcion() {
        when(cuentaRepository.existsByNumeroCuenta(anyString())).thenReturn(false);
        when(tipoCuentaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cuentaService.crearCuenta(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tipo de cuenta");
    }

    @Test
    @DisplayName("crearCuenta: cuando Feign responde 404 (cliente inexistente), lanza BusinessRuleException")
    void crearCuenta_cuandoClienteRemotoNoExiste_lanzaBusinessRuleException() {
        when(cuentaRepository.existsByNumeroCuenta(anyString())).thenReturn(false);
        when(tipoCuentaRepository.findById(anyLong())).thenReturn(Optional.of(tipoCuenta));
        when(clienteFeignClient.obtenerCliente(anyString())).thenThrow(feignNotFound());

        assertThatThrownBy(() -> cuentaService.crearCuenta(requestDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("no existe");

        verify(cuentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearCuenta: cuando Feign falla (ms-clientes caido), lanza RemoteServiceException")
    void crearCuenta_cuandoMsClientesCaido_lanzaRemoteServiceException() {
        when(cuentaRepository.existsByNumeroCuenta(anyString())).thenReturn(false);
        when(tipoCuentaRepository.findById(anyLong())).thenReturn(Optional.of(tipoCuenta));
        when(clienteFeignClient.obtenerCliente(anyString())).thenThrow(feignServerError());

        assertThatThrownBy(() -> cuentaService.crearCuenta(requestDTO))
                .isInstanceOf(RemoteServiceException.class)
                .hasMessageContaining("ms-clientes");
    }

    @Test
    @DisplayName("obtenerCuentaPorId: cuando existe, retorna la cuenta")
    void obtenerCuentaPorId_cuandoExiste_retornaCuenta() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));

        CuentaResponseDTO resultado = cuentaService.obtenerCuentaPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCuenta()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerCuentaPorId: cuando no existe, lanza ResourceNotFoundException")
    void obtenerCuentaPorId_cuandoNoExiste_lanzaExcepcion() {
        when(cuentaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cuentaService.obtenerCuentaPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cuenta no encontrada");
    }

    @Test
    @DisplayName("depositar: cuando la cuenta esta ACTIVA, suma el monto al saldo")
    void depositar_conCuentaActiva_sumaSaldo() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaActiva);

        CuentaResponseDTO resultado = cuentaService.depositar(1L, montoDTO);

        assertThat(resultado.getSaldo()).isEqualByComparingTo(new BigDecimal("550000.00"));
        verify(cuentaRepository).save(cuentaActiva);
    }

    @Test
    @DisplayName("depositar: cuando la cuenta NO esta activa, lanza BusinessRuleException")
    void depositar_conCuentaNoActiva_lanzaExcepcion() {
        cuentaActiva.setEstado(EstadoCuenta.BLOQUEADA);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));

        assertThatThrownBy(() -> cuentaService.depositar(1L, montoDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("no esta activa");

        verify(cuentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("retirar: cuando hay saldo suficiente, lo descuenta")
    void retirar_conSaldoSuficiente_descuentaSaldo() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaActiva);

        CuentaResponseDTO resultado = cuentaService.retirar(1L, montoDTO);

        assertThat(resultado.getSaldo()).isEqualByComparingTo(new BigDecimal("450000.00"));
    }

    @Test
    @DisplayName("retirar: cuando NO hay saldo suficiente, lanza BusinessRuleException")
    void retirar_conSaldoInsuficiente_lanzaExcepcion() {
        montoDTO.setMonto(new BigDecimal("999999999.00"));
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));

        assertThatThrownBy(() -> cuentaService.retirar(1L, montoDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Saldo insuficiente");

        verify(cuentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("transferir: con cuentas validas y saldo suficiente, transfiere")
    void transferir_conDatosValidos_transfiere() {
        Cuenta destino = new Cuenta();
        destino.setIdCuenta(2L);
        destino.setNumeroCuenta("22222222");
        destino.setSaldo(new BigDecimal("100000.00"));
        destino.setEstado(EstadoCuenta.ACTIVA);
        destino.setRutCliente("22222222-2");
        destino.setTipoCuenta(tipoCuenta);

        TransferenciaRequestDTO transferDTO = new TransferenciaRequestDTO();
        transferDTO.setIdCuentaOrigen(1L);
        transferDTO.setIdCuentaDestino(2L);
        transferDTO.setMonto(new BigDecimal("50000.00"));

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));
        when(cuentaRepository.findById(2L)).thenReturn(Optional.of(destino));

        TransferenciaResponseDTO resultado = cuentaService.transferir(transferDTO);

        assertThat(resultado).isNotNull();
        assertThat(cuentaActiva.getSaldo()).isEqualByComparingTo(new BigDecimal("450000.00"));
        assertThat(destino.getSaldo()).isEqualByComparingTo(new BigDecimal("150000.00"));
        verify(cuentaRepository).save(cuentaActiva);
        verify(cuentaRepository).save(destino);
    }

    @Test
    @DisplayName("transferir: cuando origen y destino son iguales, lanza BusinessRuleException")
    void transferir_conMismaCuenta_lanzaExcepcion() {
        TransferenciaRequestDTO transferDTO = new TransferenciaRequestDTO();
        transferDTO.setIdCuentaOrigen(1L);
        transferDTO.setIdCuentaDestino(1L);
        transferDTO.setMonto(new BigDecimal("50000.00"));

        assertThatThrownBy(() -> cuentaService.transferir(transferDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("distintas");

        verify(cuentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("transferir: cuando origen no tiene saldo suficiente, lanza BusinessRuleException")
    void transferir_sinSaldoEnOrigen_lanzaExcepcion() {
        Cuenta destino = new Cuenta();
        destino.setIdCuenta(2L);
        destino.setSaldo(BigDecimal.ZERO);
        destino.setEstado(EstadoCuenta.ACTIVA);

        TransferenciaRequestDTO transferDTO = new TransferenciaRequestDTO();
        transferDTO.setIdCuentaOrigen(1L);
        transferDTO.setIdCuentaDestino(2L);
        transferDTO.setMonto(new BigDecimal("999999999.00"));

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));
        when(cuentaRepository.findById(2L)).thenReturn(Optional.of(destino));

        assertThatThrownBy(() -> cuentaService.transferir(transferDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Saldo insuficiente");

        verify(cuentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("bloquearCuenta: cuando esta ACTIVA, cambia el estado a BLOQUEADA")
    void bloquearCuenta_cuandoActiva_laBloquea() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaActiva);

        CuentaResponseDTO resultado = cuentaService.bloquearCuenta(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCuenta.BLOQUEADA);
    }

    @Test
    @DisplayName("bloquearCuenta: cuando ya esta BLOQUEADA, lanza BusinessRuleException")
    void bloquearCuenta_cuandoYaBloqueada_lanzaExcepcion() {
        cuentaActiva.setEstado(EstadoCuenta.BLOQUEADA);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));

        assertThatThrownBy(() -> cuentaService.bloquearCuenta(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ya esta bloqueada");
    }

    @Test
    @DisplayName("eliminarCuenta: cuando tiene saldo positivo, lanza BusinessRuleException")
    void eliminarCuenta_conSaldoPositivo_lanzaExcepcion() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaActiva));

        assertThatThrownBy(() -> cuentaService.eliminarCuenta(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("saldo positivo");

        verify(cuentaRepository, never()).delete(any());
    }

    // ----- Helpers para construir FeignException reales -----

    private FeignException feignNotFound() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://ms-clientes/api/v1/clientes/11111111-1",
                new HashMap<>(),
                null,
                new RequestTemplate()
        );
        return new FeignException.NotFound(
                "Cliente no encontrado",
                request,
                "Not Found".getBytes(StandardCharsets.UTF_8),
                new HashMap<>()
        );
    }

    private FeignException feignServerError() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://ms-clientes/api/v1/clientes/11111111-1",
                new HashMap<>(),
                null,
                new RequestTemplate()
        );
        return new FeignException.InternalServerError(
                "ms-clientes caido",
                request,
                "Internal Server Error".getBytes(StandardCharsets.UTF_8),
                new HashMap<>()
        );
    }
}
