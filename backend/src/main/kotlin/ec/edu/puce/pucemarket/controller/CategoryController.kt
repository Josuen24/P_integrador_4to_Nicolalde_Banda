package ec.edu.puce.pucemarket.controller

import ec.edu.puce.pucemarket.dto.category.CategoryResponse
import ec.edu.puce.pucemarket.service.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/categories")
class CategoryController(private val categoryService: CategoryService) {
    @GetMapping
    fun getCategories(): List<CategoryResponse> = categoryService.getActiveCategories()
}
