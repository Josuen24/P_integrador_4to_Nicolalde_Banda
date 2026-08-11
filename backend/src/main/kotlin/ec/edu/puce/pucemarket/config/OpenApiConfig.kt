package ec.edu.puce.pucemarket.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun puceMarketOpenApi(): OpenAPI = OpenAPI()
        .info(Info().title("PUCE Market API").version("v1").description("Marketplace universitario PUCE"))
        .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
        .components(
            io.swagger.v3.oas.models.Components().addSecuritySchemes(
                "bearerAuth",
                SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"),
            ),
        )
}
