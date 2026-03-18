package com.example.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Production REST API Template")
                        .version("1.0.0")
                        .description("REST API showing cursor pagination, global exception handling RFC 7807, and validation.")
                        .contact(new Contact().name("Backend Team").email("team@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
