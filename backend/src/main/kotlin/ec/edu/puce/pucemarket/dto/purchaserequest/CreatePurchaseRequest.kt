package ec.edu.puce.pucemarket.dto.purchaserequest

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreatePurchaseRequest(
    @field:NotNull(message = "El precio ofertado es obligatorio")
    @field:DecimalMin(value = "0.01", message = "El precio ofertado debe ser mayor que cero")
    val offeredPrice: BigDecimal?,

    @field:Size(max = 1_000, message = "El mensaje no puede exceder 1000 caracteres")
    val message: String? = null,
)
