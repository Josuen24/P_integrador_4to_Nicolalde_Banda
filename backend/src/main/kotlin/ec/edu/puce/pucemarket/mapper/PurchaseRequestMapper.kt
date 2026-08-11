package ec.edu.puce.pucemarket.mapper

import ec.edu.puce.pucemarket.dto.purchaserequest.PurchaseRequestResponse
import ec.edu.puce.pucemarket.entity.Product
import ec.edu.puce.pucemarket.entity.PurchaseRequest
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class PurchaseRequestMapper {
    fun toEntity(
        offeredPrice: BigDecimal,
        message: String?,
        buyerUsername: String,
        product: Product,
    ): PurchaseRequest = PurchaseRequest(
        offeredPrice = offeredPrice,
        message = message?.trim()?.takeIf { it.isNotEmpty() },
        buyerUsername = buyerUsername,
        product = product,
    )

    fun toResponse(request: PurchaseRequest): PurchaseRequestResponse = PurchaseRequestResponse(
        id = requireNotNull(request.id),
        productId = requireNotNull(request.product.id),
        offeredPrice = request.offeredPrice,
        message = request.message,
        status = request.status,
        buyerUsername = request.buyerUsername,
        createdAt = requireNotNull(request.createdAt),
        updatedAt = requireNotNull(request.updatedAt),
        respondedAt = request.respondedAt,
    )
}
