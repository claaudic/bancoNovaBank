package com.novabank.mscuentas.client;

import com.novabank.mscuentas.dto.response.ClienteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-clientes", url = "${ms-clientes.url}")
public interface ClienteFeignClient {

    @GetMapping("/api/v1/clientes/{rutCliente}")
    ClienteResponseDTO obtenerCliente(@PathVariable("rutCliente") String rutCliente);
}
