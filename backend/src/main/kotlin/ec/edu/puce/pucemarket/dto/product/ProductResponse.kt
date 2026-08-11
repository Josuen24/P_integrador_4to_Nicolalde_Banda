package ec.edu.puce.pucemarket.dto.product

import ec.edu.puce.pucemarket.dto.category.CategoryResponse
import ec.edu.puce.pucemarket.enums.ProductStatus
import java.math.BigDecimal
import java.time.Instant

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val status: ProductStatus,
    val sellerUsername: String,
    val category: CategoryResponse,
    val createdAt: Instant,
    val updatedAt: Instant,
)
