package ec.edu.puce.pucemarket.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "categories")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 100)
    var name: String,

    @Column(nullable = false, length = 500)
    var description: String,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @Column(nullable = false)
    var updatedAt: Instant? = null,
) {
    @OneToMany(mappedBy = "category")
    val products: MutableSet<Product> = mutableSetOf()

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
