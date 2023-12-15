package com.example.resourceservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
            return new OpenAPI()
                    .components(new Components().addSecuritySchemes("basicScheme",
                            new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                    .info(apiInfo());
        }

        private Info apiInfo() {
            return new Info().title("Resource Service API")
                    .version("1.0")
                    .description("The API documentation for the Resource Service")
                    .contact(new Contact()
                            .name("Andrii Roman")
                            .email("andrewroman1996@gmail.com"));
        }
}
