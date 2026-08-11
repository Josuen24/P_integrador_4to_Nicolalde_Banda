package ec.edu.puce.pucemarket.security

import com.fasterxml.jackson.databind.ObjectMapper
import ec.edu.puce.pucemarket.exception.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class RestAuthenticationEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException) {
        write(response, HttpStatus.UNAUTHORIZED, "Se requiere un token JWT válido", request.requestURI)
    }

    private fun write(response: HttpServletResponse, status: HttpStatus, message: String, path: String) {
        response.status = status.value()
        response.contentType = "application/json"
        objectMapper.writeValue(response.outputStream, ErrorResponse(status = status.value(), error = status.reasonPhrase, message = message, path = path))
    }
}

@Component
class RestAccessDeniedHandler(private val objectMapper: ObjectMapper) : AccessDeniedHandler {
    override fun handle(request: HttpServletRequest, response: HttpServletResponse, exception: AccessDeniedException) {
        response.status = HttpStatus.FORBIDDEN.value()
        response.contentType = "application/json"
        objectMapper.writeValue(
            response.outputStream,
            ErrorResponse(status = 403, error = "Forbidden", message = "No tienes permiso para esta acción", path = request.requestURI),
        )
    }
}
