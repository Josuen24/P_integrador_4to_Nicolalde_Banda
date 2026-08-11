package ec.edu.puce.pucemarket.mapper

import ec.edu.puce.pucemarket.dto.product.ProductResponse
import ec.edu.puce.pucemarket.entity.Category
import ec.edu.puce.pucemarket.entity.Product
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ProductMapper(
    private val categoryMapper: CategoryMapper,
) {
    fun toEntity(
        name: String,
        description: String,
        price: BigDecimal,
        sellerUsername: String,
        category: Category,
    ): Product = Product(
        name = name.trim(),
        description = description.trim(),
        price = price,
        sellerUsername = sellerUsername,
        category = category,
    )

    fun toResponse(product: Product): ProductResponse = ProductResponse(
        id = requireNotNull(product.id),
        name = product.name,
        description = product.description,
        price = product.price,
        status = product.status,
        sellerUsername = product.sellerUsername,
        category = categoryMapper.toResponse(product.category),
        createdAt = requireNotNull(product.createdAt),
        updatedAt = requireNotNull(product.updatedAt),
    )
}
