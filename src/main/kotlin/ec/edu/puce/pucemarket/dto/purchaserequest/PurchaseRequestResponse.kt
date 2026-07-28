package ec.edu.puce.pucemarket.dto.purchaserequest

import ec.edu.puce.pucemarket.enums.PurchaseRequestStatus
import java.math.BigDecimal
import java.time.Instant

data class PurchaseRequestResponse(
    val id: Long,
    val productId: Long,
    val offeredPrice: BigDecimal,
    val message: String?,
    val status: PurchaseRequestStatus,
    val buyerUsername: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val respondedAt: Instant?,
)
