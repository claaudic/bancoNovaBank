package com.novabank.msorganizacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI msOrganizacionOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("ms-organizacion")
                .description("API REST para la gestion de sucursales, ejecutivos y direcciones de sucursal")
                .version("1.0.0"));
    }
}
