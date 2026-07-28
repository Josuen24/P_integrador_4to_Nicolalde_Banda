package ec.edu.puce.pucemarket.service

import ec.edu.puce.pucemarket.dto.category.CategoryResponse

interface CategoryService {
    fun getActiveCategories(): List<CategoryResponse>
}
