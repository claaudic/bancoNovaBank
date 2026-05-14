package com.novabank.mstransacciones.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI msTransaccionesOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Banco NovaBank - ms-transacciones")
                .description("API REST para el registro y orquestacion de transacciones financieras")
                .version("1.0.0")
                .contact(new Contact().name("Equipo NovaBank")));
    }
}
