package com.novabank.msclientes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI msClientesOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Banco NovaBank - ms-clientes")
                .description("API REST para la gestion de clientes, profesiones, direcciones y usuarios")
                .version("1.0.0")
                .contact(new Contact().name("Equipo NovaBank")));
    }
}
