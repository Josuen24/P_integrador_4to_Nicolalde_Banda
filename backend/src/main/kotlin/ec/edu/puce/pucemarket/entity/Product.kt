package ec.edu.puce.pucemarket.entity

import ec.edu.puce.pucemarket.enums.ProductStatus
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
import jakarta.persistence.OneToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 150)
    var name: String,

    @Column(nullable = false, length = 2_000)
    var description: String,

    @Column(nullable = false, precision = 12, scale = 2)
    var price: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: ProductStatus = ProductStatus.AVAILABLE,

    @Column(nullable = false, length = 100)
    var sellerUsername: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @Column(nullable = false)
    var updatedAt: Instant? = null,
) {
    @OneToMany(mappedBy = "product")
    val purchaseRequests: MutableSet<PurchaseRequest> = mutableSetOf()

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
