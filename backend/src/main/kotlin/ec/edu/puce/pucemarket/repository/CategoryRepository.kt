package ec.edu.puce.pucemarket.repository

import ec.edu.puce.pucemarket.entity.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {
    fun findAllByActiveTrueOrderByNameAsc(): List<Category>
}
