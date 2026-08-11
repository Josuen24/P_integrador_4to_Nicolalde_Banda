package ec.edu.puce.pucemarket.mapper

import ec.edu.puce.pucemarket.dto.category.CategoryResponse
import ec.edu.puce.pucemarket.entity.Category
import org.springframework.stereotype.Component

@Component
class CategoryMapper {
    fun toResponse(category: Category): CategoryResponse = CategoryResponse(
        id = requireNotNull(category.id),
        name = category.name,
        description = category.description,
    )
}
