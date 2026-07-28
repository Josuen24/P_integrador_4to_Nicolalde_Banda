package ec.edu.puce.pucemarket.repository

import ec.edu.puce.pucemarket.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {
    fun findAllBySellerUsernameOrderByCreatedAtDesc(sellerUsername: String): List<Product>
}
