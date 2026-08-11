package ec.edu.puce.pucemarket.service.impl

import ec.edu.puce.pucemarket.client.UserContactClient
import ec.edu.puce.pucemarket.dto.contact.BuyerContactResponse
import ec.edu.puce.pucemarket.enums.ProductStatus
import ec.edu.puce.pucemarket.enums.PurchaseRequestStatus
import ec.edu.puce.pucemarket.exception.BusinessConflictException
import ec.edu.puce.pucemarket.exception.ForbiddenOperationException
import ec.edu.puce.pucemarket.exception.ResourceNotFoundException
import ec.edu.puce.pucemarket.repository.PurchaseRequestRepository
import ec.edu.puce.pucemarket.service.BuyerContactService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BuyerContactServiceImpl(
    private val purchaseRequestRepository: PurchaseRequestRepository,
    private val userContactClient: UserContactClient,
) : BuyerContactService {
    @Transactional(readOnly = true)
    override fun getBuyerContact(requestId: Long, sellerUsername: String): BuyerContactResponse {
        val request = purchaseRequestRepository.findById(requestId)
            .orElseThrow { ResourceNotFoundException("Oferta $requestId no encontrada") }
        val product = request.product
        if (product.sellerUsername != sellerUsername) {
            throw ForbiddenOperationException("Solo el vendedor propietario puede solicitar el contacto")
        }
        if (request.status != PurchaseRequestStatus.ACCEPTED || product.status !in setOf(ProductStatus.RESERVED, ProductStatus.SOLD)) {
            throw BusinessConflictException("El contacto solo está disponible para una oferta aceptada")
        }
        val phone = userContactClient.getPhoneByUsername(request.buyerUsername)
        return BuyerContactResponse(request.buyerUsername, "https://wa.me/$phone")
    }
}
