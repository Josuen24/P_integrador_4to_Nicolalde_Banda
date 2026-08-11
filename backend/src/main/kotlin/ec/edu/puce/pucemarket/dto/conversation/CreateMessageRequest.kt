package ec.edu.puce.pucemarket.dto.conversation

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateMessageRequest(@field:NotBlank(message = "El mensaje no puede estar vacío") @field:Size(max = 1000, message = "El mensaje no puede superar 1000 caracteres") val content: String)
