package ec.edu.puce.pucemarket.dto

import ec.edu.puce.pucemarket.dto.product.CreateProductRequest
import ec.edu.puce.pucemarket.dto.purchaserequest.CreatePurchaseRequest
import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class RequestValidationTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `rejects a product with blank fields or a non-positive price`() {
        val request = CreateProductRequest(
            name = " ",
            description = " ",
            price = BigDecimal.ZERO,
            categoryId = null,
        )

        assertEquals(4, validator.validate(request).size)
    }

    @Test
    fun `rejects a purchase request with a non-positive offered price`() {
        val request = CreatePurchaseRequest(offeredPrice = BigDecimal.ZERO)

        assertEquals(1, validator.validate(request).size)
    }
}
