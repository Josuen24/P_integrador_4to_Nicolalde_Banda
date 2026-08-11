package ec.edu.puce.pucemarket.service.impl

import ec.edu.puce.pucemarket.dto.purchaserequest.CreatePurchaseRequest
import ec.edu.puce.pucemarket.dto.purchaserequest.PurchaseRequestResponse
import ec.edu.puce.pucemarket.entity.Product
import ec.edu.puce.pucemarket.entity.PurchaseRequest
import ec.edu.puce.pucemarket.enums.ProductStatus
import ec.edu.puce.pucemarket.enums.PurchaseRequestStatus
import ec.edu.puce.pucemarket.exception.DuplicatePendingRequestException
import ec.edu.puce.pucemarket.exception.ForbiddenOperationException
import ec.edu.puce.pucemarket.exception.InvalidStatusTransitionException
import ec.edu.puce.pucemarket.exception.ResourceNotFoundException
import ec.edu.puce.pucemarket.mapper.PurchaseRequestMapper
import ec.edu.puce.pucemarket.repository.ProductRepository
import ec.edu.puce.pucemarket.repository.PurchaseRequestRepository
import ec.edu.puce.pucemarket.service.PurchaseRequestService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class PurchaseRequestServiceImpl(
    private val productRepository: ProductRepository,
    private val purchaseRequestRepository: PurchaseRequestRepository,
    private val purchaseRequestMapper: PurchaseRequestMapper,
) : PurchaseRequestService {
    @Transactional
    override fun createRequest(
        productId: Long,
        request: CreatePurchaseRequest,
        buyerUsername: String,
    ): PurchaseRequestResponse {
        val product = findProduct(productId)
        if (product.sellerUsername == buyerUsername) {
            throw ForbiddenOperationException("No puedes enviar una oferta sobre tu propio producto")
        }
        ensureAvailable(product)
        if (purchaseRequestRepository.existsByProductIdAndBuyerUsernameAndStatus(
                productId,
                buyerUsername,
                PurchaseRequestStatus.PENDING,
            )
        ) {
            throw DuplicatePendingRequestException("Ya existe una oferta pendiente para este producto")
        }
        val entity = purchaseRequestMapper.toEntity(
            offeredPrice = requireNotNull(request.offeredPrice),
            message = request.message,
            buyerUsername = buyerUsername,
            product = product,
        )
        return purchaseRequestMapper.toResponse(purchaseRequestRepository.save(entity))
    }

    @Transactional(readOnly = true)
    override fun getMyRequests(buyerUsername: String): List<PurchaseRequestResponse> =
        purchaseRequestRepository.findAllByBuyerUsernameOrderByCreatedAtDesc(buyerUsername).map(purchaseRequestMapper::toResponse)

    @Transactional(readOnly = true)
    override fun getReceivedRequests(productId: Long, sellerUsername: String): List<PurchaseRequestResponse> {
        assertSeller(findProduct(productId), sellerUsername)
        return purchaseRequestRepository.findAllByProductIdOrderByCreatedAtDesc(productId).map(purchaseRequestMapper::toResponse)
    }

    @Transactional
    override fun acceptRequest(requestId: Long, sellerUsername: String): PurchaseRequestResponse {
        val request = findRequest(requestId)
        val product = productRepository.findByIdForUpdate(requireNotNull(request.product.id))
            ?: throw ResourceNotFoundException("Producto de la oferta no encontrado")
        assertSeller(product, sellerUsername)
        ensurePending(request, "Solo se puede aceptar una oferta pendiente")
        ensureAvailable(product)

        val now = Instant.now()
        request.status = PurchaseRequestStatus.ACCEPTED
        request.respondedAt = now
        product.status = ProductStatus.RESERVED
        purchaseRequestRepository.rejectOtherPendingRequests(product.id!!, request.id!!, now)
        return purchaseRequestMapper.toResponse(request)
    }

    @Transactional
    override fun rejectRequest(requestId: Long, sellerUsername: String): PurchaseRequestResponse {
        val request = findRequest(requestId)
        assertSeller(request.product, sellerUsername)
        ensurePending(request, "Solo se puede rechazar una oferta pendiente")
        request.status = PurchaseRequestStatus.REJECTED
        request.respondedAt = Instant.now()
        return purchaseRequestMapper.toResponse(request)
    }

    @Transactional
    override fun cancelRequest(requestId: Long, buyerUsername: String) {
        val request = findRequest(requestId)
        if (request.buyerUsername != buyerUsername) {
            throw ForbiddenOperationException("No eres propietario de esta oferta")
        }
        ensurePending(request, "Solo se puede cancelar una oferta pendiente")
        request.status = PurchaseRequestStatus.CANCELLED
        request.respondedAt = Instant.now()
    }

    private fun findProduct(productId: Long): Product = productRepository.findById(productId)
        .orElseThrow { ResourceNotFoundException("Producto $productId no encontrado") }

    private fun findRequest(requestId: Long): PurchaseRequest = purchaseRequestRepository.findById(requestId)
        .orElseThrow { ResourceNotFoundException("Oferta $requestId no encontrada") }

    private fun assertSeller(product: Product, username: String) {
        if (product.sellerUsername != username) {
            throw ForbiddenOperationException("No eres propietario de este producto")
        }
    }

    private fun ensureAvailable(product: Product) {
        if (product.status != ProductStatus.AVAILABLE) {
            throw InvalidStatusTransitionException("Solo se puede ofertar o aceptar sobre un producto disponible")
        }
    }

    private fun ensurePending(request: PurchaseRequest, message: String) {
        if (request.status != PurchaseRequestStatus.PENDING) {
            throw InvalidStatusTransitionException(message)
        }
    }
}
