package com.erp.base.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
@ComponentScan(basePackages = "com.erp.base.controller")
public class SwaggerConfig {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API DOC")
                        .description("API DOC")
                        .version("v1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
