package ec.edu.puce.pucemarket.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(exception: ResourceNotFoundException, request: HttpServletRequest) =
        error(HttpStatus.NOT_FOUND, exception.message.orEmpty(), request)

    @ExceptionHandler(ForbiddenOperationException::class)
    fun handleForbidden(exception: ForbiddenOperationException, request: HttpServletRequest) =
        error(HttpStatus.FORBIDDEN, exception.message.orEmpty(), request)

    @ExceptionHandler(BusinessConflictException::class)
    fun handleConflict(exception: BusinessConflictException, request: HttpServletRequest) =
        error(HttpStatus.CONFLICT, exception.message.orEmpty(), request)

    @ExceptionHandler(ExternalServiceException::class)
    fun handleExternalService(exception: ExternalServiceException, request: HttpServletRequest) =
        error(HttpStatus.BAD_GATEWAY, exception.message.orEmpty(), request)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(exception: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val validationErrors = exception.bindingResult.allErrors
            .filterIsInstance<FieldError>()
            .associate { it.field to (it.defaultMessage ?: "Valor inválido") }
        return error(HttpStatus.BAD_REQUEST, "La solicitud contiene datos inválidos", request, validationErrors)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableMessage(request: HttpServletRequest) =
        error(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es válido", request)

    private fun error(
        status: HttpStatus,
        message: String,
        request: HttpServletRequest,
        validationErrors: Map<String, String>? = null,
    ): ResponseEntity<ErrorResponse> = ResponseEntity.status(status).body(
        ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = request.requestURI,
            validationErrors = validationErrors,
        ),
    )
}
