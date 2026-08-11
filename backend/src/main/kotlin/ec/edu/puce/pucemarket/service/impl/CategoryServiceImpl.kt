package ec.edu.puce.pucemarket.service.impl

import ec.edu.puce.pucemarket.dto.category.CategoryResponse
import ec.edu.puce.pucemarket.mapper.CategoryMapper
import ec.edu.puce.pucemarket.repository.CategoryRepository
import ec.edu.puce.pucemarket.service.CategoryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val categoryMapper: CategoryMapper,
) : CategoryService {
    @Transactional(readOnly = true)
    override fun getActiveCategories(): List<CategoryResponse> =
        categoryRepository.findAllByActiveTrueOrderByNameAsc().map(categoryMapper::toResponse)
}
