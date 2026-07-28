package ec.edu.puce.pucemarket.service

import ec.edu.puce.pucemarket.dto.product.UpdateProductRequest
import ec.edu.puce.pucemarket.entity.Category
import ec.edu.puce.pucemarket.entity.Product
import ec.edu.puce.pucemarket.enums.ProductStatus
import ec.edu.puce.pucemarket.exception.ForbiddenOperationException
import ec.edu.puce.pucemarket.exception.InvalidStatusTransitionException
import ec.edu.puce.pucemarket.mapper.CategoryMapper
import ec.edu.puce.pucemarket.mapper.ProductMapper
import ec.edu.puce.pucemarket.repository.CategoryRepository
import ec.edu.puce.pucemarket.repository.ProductRepository
import ec.edu.puce.pucemarket.service.impl.ProductServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.time.Instant
import java.util.Optional

class ProductServiceImplTest {
    private val productRepository = mock(ProductRepository::class.java)
    private val categoryRepository = mock(CategoryRepository::class.java)
    private val service = ProductServiceImpl(productRepository, categoryRepository, ProductMapper(CategoryMapper()))

    @Test
    fun `prevents a non-owner from editing a product`() {
        `when`(productRepository.findById(7)).thenReturn(Optional.of(product()))

        assertThrows(ForbiddenOperationException::class.java) {
            service.updateProduct(7, updateRequest(), "other.puce")
        }
    }

    @Test
    fun `only allows a reserved product to become sold`() {
        `when`(productRepository.findById(7)).thenReturn(Optional.of(product()))

        assertThrows(InvalidStatusTransitionException::class.java) {
            service.markAsSold(7, "seller.puce")
        }
    }

    @Test
    fun `marks a reserved product as sold for its owner`() {
        val product = product().apply { status = ProductStatus.RESERVED }
        `when`(productRepository.findById(7)).thenReturn(Optional.of(product))

        val response = service.markAsSold(7, "seller.puce")

        assertEquals(ProductStatus.SOLD, response.status)
    }

    private fun product(): Product = Product(
        id = 7,
        name = "Calculadora",
        description = "En buen estado",
        price = BigDecimal("25.00"),
        sellerUsername = "seller.puce",
        category = Category(id = 1, name = "Tecnología", description = "Accesorios"),
        createdAt = NOW,
        updatedAt = NOW,
    )

    private fun updateRequest() = UpdateProductRequest(
        name = "Calculadora actualizada",
        description = "En buen estado",
        price = BigDecimal("30.00"),
        categoryId = 1,
    )

    private companion object {
        val NOW: Instant = Instant.parse("2026-01-01T00:00:00Z")
    }
}
