package com.novabank.mscuentas.dto.response;

import com.novabank.mscuentas.model.Cuenta;
import com.novabank.mscuentas.model.EstadoCuenta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CuentaResponseDTO extends RepresentationModel<CuentaResponseDTO> {

    private Long idCuenta;
    private String numeroCuenta;
    private LocalDate fechaCreacion;
    private BigDecimal saldo;
    private String rutCliente;
    private EstadoCuenta estado;
    private TipoCuentaResponseDTO tipoCuenta;

    public static CuentaResponseDTO toResponseDTO(Cuenta cuenta) {
        CuentaResponseDTO response = new CuentaResponseDTO();
        response.setIdCuenta(cuenta.getIdCuenta());
        response.setNumeroCuenta(cuenta.getNumeroCuenta());
        response.setFechaCreacion(cuenta.getFechaCreacion());
        response.setSaldo(cuenta.getSaldo());
        response.setRutCliente(cuenta.getRutCliente());
        response.setEstado(cuenta.getEstado());
        response.setTipoCuenta(TipoCuentaResponseDTO.toResponseDTO(cuenta.getTipoCuenta()));
        return response;
    }
}
