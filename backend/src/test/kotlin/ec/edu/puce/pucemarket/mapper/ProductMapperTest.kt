package ec.edu.puce.pucemarket.mapper

import ec.edu.puce.pucemarket.entity.Category
import ec.edu.puce.pucemarket.entity.Product
import ec.edu.puce.pucemarket.enums.ProductStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class ProductMapperTest {
    private val mapper = ProductMapper(CategoryMapper())

    @Test
    fun `maps a product entity without exposing the entity itself`() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val category = Category(id = 4, name = "Libros", description = "Textos universitarios")
        val product = Product(
            id = 9,
            name = "Álgebra",
            description = "Libro usado",
            price = BigDecimal("12.50"),
            status = ProductStatus.AVAILABLE,
            sellerUsername = "seller.puce",
            category = category,
            createdAt = now,
            updatedAt = now,
        )

        val response = mapper.toResponse(product)

        assertEquals(9, response.id)
        assertEquals("Libros", response.category.name)
        assertEquals("seller.puce", response.sellerUsername)
    }
}
