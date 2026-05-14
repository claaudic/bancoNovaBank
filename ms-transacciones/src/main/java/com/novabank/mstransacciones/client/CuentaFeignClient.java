package com.novabank.mstransacciones.client;

import com.novabank.mstransacciones.dto.request.MontoFeignRequest;
import com.novabank.mstransacciones.dto.response.CuentaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-cuentas", url = "${ms-cuentas.url}")
public interface CuentaFeignClient {

    @GetMapping("/api/v1/cuentas/{idCuenta}")
    CuentaResponseDTO obtenerCuenta(@PathVariable("idCuenta") Long idCuenta);

    @PostMapping("/api/v1/cuentas/{idCuenta}/depositar")
    CuentaResponseDTO depositar(@PathVariable("idCuenta") Long idCuenta, @RequestBody MontoFeignRequest dto);

    @PostMapping("/api/v1/cuentas/{idCuenta}/retirar")
    CuentaResponseDTO retirar(@PathVariable("idCuenta") Long idCuenta, @RequestBody MontoFeignRequest dto);
}
