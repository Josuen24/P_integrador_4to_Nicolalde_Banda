package ec.edu.puce.pucemarket.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "conversations")
class Conversation(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "purchase_request_id", nullable = false, unique = true)
    var purchaseRequest: PurchaseRequest,
    @Column(nullable = false, length = 100) var buyerUsername: String,
    @Column(nullable = false, length = 100) var sellerUsername: String,
    @Column(nullable = false, updatable = false) var createdAt: Instant? = null,
    @Column(nullable = false) var updatedAt: Instant? = null,
) {
    @PrePersist fun onCreate() { val now = Instant.now(); createdAt = now; updatedAt = now }
    @PreUpdate fun onUpdate() { updatedAt = Instant.now() }
}
