package com.novabank.mscuentas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI msCuentasOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Banco NovaBank - ms-cuentas")
                .description("API REST para la gestion de cuentas, tipos de cuenta y tarjetas")
                .version("1.0.0")
                .contact(new Contact().name("Equipo NovaBank")));
    }
}
