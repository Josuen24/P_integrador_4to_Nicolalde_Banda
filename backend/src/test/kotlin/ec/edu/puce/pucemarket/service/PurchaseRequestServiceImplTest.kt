package ec.edu.puce.pucemarket.service

import ec.edu.puce.pucemarket.dto.purchaserequest.CreatePurchaseRequest
import ec.edu.puce.pucemarket.entity.Category
import ec.edu.puce.pucemarket.entity.Product
import ec.edu.puce.pucemarket.entity.PurchaseRequest
import ec.edu.puce.pucemarket.enums.ProductStatus
import ec.edu.puce.pucemarket.enums.PurchaseRequestStatus
import ec.edu.puce.pucemarket.exception.DuplicatePendingRequestException
import ec.edu.puce.pucemarket.exception.ForbiddenOperationException
import ec.edu.puce.pucemarket.exception.InvalidStatusTransitionException
import ec.edu.puce.pucemarket.mapper.PurchaseRequestMapper
import ec.edu.puce.pucemarket.repository.ProductRepository
import ec.edu.puce.pucemarket.repository.PurchaseRequestRepository
import ec.edu.puce.pucemarket.service.impl.PurchaseRequestServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.time.Instant
import java.util.Optional

class PurchaseRequestServiceImplTest {
    private val productRepository = mock(ProductRepository::class.java)
    private val requestRepository = mock(PurchaseRequestRepository::class.java)
    private val service = PurchaseRequestServiceImpl(productRepository, requestRepository, PurchaseRequestMapper())

    @Test
    fun `prevents a buyer from offering on their own product`() {
        val product = product()
        `when`(productRepository.findById(7)).thenReturn(Optional.of(product))

        assertThrows(ForbiddenOperationException::class.java) {
            service.createRequest(7, CreatePurchaseRequest(BigDecimal("20.00")), "seller.puce")
        }
        verify(requestRepository, never()).save(org.mockito.ArgumentMatchers.any(PurchaseRequest::class.java))
    }

    @Test
    fun `prevents duplicate pending requests`() {
        `when`(productRepository.findById(7)).thenReturn(Optional.of(product()))
        `when`(
            requestRepository.existsByProductIdAndBuyerUsernameAndStatus(
                7,
                "buyer.puce",
                PurchaseRequestStatus.PENDING,
            ),
        ).thenReturn(true)

        assertThrows(DuplicatePendingRequestException::class.java) {
            service.createRequest(7, CreatePurchaseRequest(BigDecimal("20.00")), "buyer.puce")
        }
    }

    @Test
    fun `accepting one request reserves product and rejects remaining pending requests`() {
        val product = product()
        val request = purchaseRequest(product)
        `when`(requestRepository.findById(4)).thenReturn(Optional.of(request))
        `when`(productRepository.findByIdForUpdate(7)).thenReturn(product)

        val response = service.acceptRequest(4, "seller.puce")

        assertEquals(PurchaseRequestStatus.ACCEPTED, request.status)
        assertEquals(ProductStatus.RESERVED, product.status)
        assertEquals(PurchaseRequestStatus.ACCEPTED, response.status)
        verify(requestRepository).rejectOtherPendingRequests(
            7L,
            4L,
            requireNotNull(request.respondedAt),
        )
    }

    @Test
    fun `does not allow marking a non-pending request as cancelled`() {
        val request = purchaseRequest(product()).apply { status = PurchaseRequestStatus.ACCEPTED }
        `when`(requestRepository.findById(4)).thenReturn(Optional.of(request))

        assertThrows(InvalidStatusTransitionException::class.java) {
            service.cancelRequest(4, "buyer.puce")
        }
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

    private fun purchaseRequest(product: Product): PurchaseRequest = PurchaseRequest(
        id = 4,
        offeredPrice = BigDecimal("20.00"),
        buyerUsername = "buyer.puce",
        product = product,
        createdAt = NOW,
        updatedAt = NOW,
    )

    private companion object {
        val NOW: Instant = Instant.parse("2026-01-01T00:00:00Z")
    }
}
