package ec.edu.puce.pucemarket.config

import ec.edu.puce.pucemarket.security.CognitoJwtAuthenticationConverter
import ec.edu.puce.pucemarket.security.RestAccessDeniedHandler
import ec.edu.puce.pucemarket.security.RestAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val authenticationEntryPoint: RestAuthenticationEntryPoint,
    private val accessDeniedHandler: RestAccessDeniedHandler,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf { it.disable() }
        .authorizeHttpRequests {
            it.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
            it.requestMatchers(HttpMethod.GET, "/api/categories", "/api/products", "/api/products/search", "/api/products/*").permitAll()
            it.anyRequest().authenticated()
        }
        .exceptionHandling {
            it.authenticationEntryPoint(authenticationEntryPoint)
            it.accessDeniedHandler(accessDeniedHandler)
        }
        .oauth2ResourceServer { resourceServer ->
            resourceServer.jwt { jwt -> jwt.jwtAuthenticationConverter(CognitoJwtAuthenticationConverter()) }
        }
        .build()
}
