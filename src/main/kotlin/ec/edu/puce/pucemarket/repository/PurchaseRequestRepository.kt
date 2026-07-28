package ec.edu.puce.pucemarket.repository

import ec.edu.puce.pucemarket.entity.PurchaseRequest
import ec.edu.puce.pucemarket.enums.PurchaseRequestStatus
import org.springframework.data.jpa.repository.JpaRepository

interface PurchaseRequestRepository : JpaRepository<PurchaseRequest, Long> {
    fun existsByProductIdAndBuyerUsernameAndStatus(
        productId: Long,
        buyerUsername: String,
        status: PurchaseRequestStatus,
    ): Boolean

    fun findAllByBuyerUsernameOrderByCreatedAtDesc(buyerUsername: String): List<PurchaseRequest>

    fun findAllByProductIdOrderByCreatedAtDesc(productId: Long): List<PurchaseRequest>
}
