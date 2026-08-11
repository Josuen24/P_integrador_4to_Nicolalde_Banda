package ec.edu.puce.pucemarket.repository

import ec.edu.puce.pucemarket.entity.Category
import ec.edu.puce.pucemarket.entity.Product
import ec.edu.puce.pucemarket.entity.PurchaseRequest
import ec.edu.puce.pucemarket.enums.ProductStatus
import ec.edu.puce.pucemarket.enums.PurchaseRequestStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal

@DataJpaTest
@ActiveProfiles("test")
class MarketplaceRepositoryTest(
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val productRepository: ProductRepository,
    @Autowired private val purchaseRequestRepository: PurchaseRequestRepository,
) {
    @Test
    fun `persists the approved domain relationships with their initial statuses`() {
        val category = categoryRepository.save(
            Category(name = "Tecnología", description = "Dispositivos y accesorios"),
        )
        val product = productRepository.save(
            Product(
                name = "Calculadora científica",
                description = "En buen estado",
                price = BigDecimal("25.00"),
                sellerUsername = "seller.puce",
                category = category,
            ),
        )
        val request = purchaseRequestRepository.save(
            PurchaseRequest(
                offeredPrice = BigDecimal("20.00"),
                buyerUsername = "buyer.puce",
                product = product,
            ),
        )

        assertNotNull(category.id)
        assertNotNull(product.id)
        assertNotNull(request.id)
        assertEquals(ProductStatus.AVAILABLE, product.status)
        assertEquals(PurchaseRequestStatus.PENDING, request.status)
        assertEquals(listOf(product), productRepository.findAllBySellerUsernameOrderByCreatedAtDesc("seller.puce"))
        assertEquals(listOf(request), purchaseRequestRepository.findAllByProductIdOrderByCreatedAtDesc(product.id!!))
    }
}
