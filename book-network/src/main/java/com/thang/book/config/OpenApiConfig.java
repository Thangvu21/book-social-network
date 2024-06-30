package com.thang.book.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
            contact = @Contact(
                    name = "thangVu",
                    email = "vuducthang212004@gmail.com",
                    url = "https://thangvu21"
            ),
            description = "spring",
            title = "OpenApi documentation for Spring Security",
            version = "1.0",
            license = @License(
                    name = "License name",
                    url = "https://some-url.com"
            ),
            termsOfService = "term of Service"),
            servers = {
                    @Server(
                            description = "Local ENV",
                            url = "http://localhost:8088/api/v1"
                    ),
                    @Server(
                            description = "PROVED"

                    )
            },
            security = {
                    @SecurityRequirement(
                            name = "bearerAuth"
                    )
            }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "jwt auth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
