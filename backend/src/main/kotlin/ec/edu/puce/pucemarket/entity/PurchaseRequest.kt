package ec.edu.puce.pucemarket.entity

import ec.edu.puce.pucemarket.enums.PurchaseRequestStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "purchase_requests")
class PurchaseRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, precision = 12, scale = 2)
    var offeredPrice: BigDecimal,

    @Column(length = 1_000)
    var message: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: PurchaseRequestStatus = PurchaseRequestStatus.PENDING,

    @Column(nullable = false, length = 100)
    var buyerUsername: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @Column(nullable = false)
    var updatedAt: Instant? = null,

    @Column
    var respondedAt: Instant? = null,
) {
    @PrePersist
    fun setCreationAuditFields() {
        val now = Instant.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun setUpdateAuditField() {
        updatedAt = Instant.now()
    }
}
