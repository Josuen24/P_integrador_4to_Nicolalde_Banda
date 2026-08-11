package ec.edu.puce.pucemarket.repository

import ec.edu.puce.pucemarket.entity.PurchaseRequest
import ec.edu.puce.pucemarket.enums.PurchaseRequestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface PurchaseRequestRepository : JpaRepository<PurchaseRequest, Long> {
    fun existsByProductIdAndBuyerUsernameAndStatus(
        productId: Long,
        buyerUsername: String,
        status: PurchaseRequestStatus,
    ): Boolean

    fun findAllByBuyerUsernameOrderByCreatedAtDesc(buyerUsername: String): List<PurchaseRequest>

    fun findAllByProductIdOrderByCreatedAtDesc(productId: Long): List<PurchaseRequest>

    @Modifying
    @Query(
        """
        update PurchaseRequest request
        set request.status = ec.edu.puce.pucemarket.enums.PurchaseRequestStatus.REJECTED,
            request.respondedAt = :respondedAt
        where request.product.id = :productId
          and request.status = ec.edu.puce.pucemarket.enums.PurchaseRequestStatus.PENDING
          and request.id <> :acceptedRequestId
        """,
    )
    fun rejectOtherPendingRequests(
        @Param("productId") productId: Long,
        @Param("acceptedRequestId") acceptedRequestId: Long,
        @Param("respondedAt") respondedAt: Instant,
    ): Int
}
