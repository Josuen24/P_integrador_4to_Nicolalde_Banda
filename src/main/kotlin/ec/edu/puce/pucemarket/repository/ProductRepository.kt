package ec.edu.puce.pucemarket.repository

import ec.edu.puce.pucemarket.entity.Product
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {
    fun findAllBySellerUsernameOrderByCreatedAtDesc(sellerUsername: String): List<Product>

    fun findAllByStatusOrderByCreatedAtDesc(status: ec.edu.puce.pucemarket.enums.ProductStatus): List<Product>

    @Query(
        """
        select product from Product product
        where product.status = ec.edu.puce.pucemarket.enums.ProductStatus.AVAILABLE
          and (:categoryId is null or product.category.id = :categoryId)
          and (:term is null or lower(product.name) like lower(concat('%', :term, '%'))
               or lower(product.description) like lower(concat('%', :term, '%')))
        order by product.createdAt desc
        """,
    )
    fun searchAvailable(
        @Param("categoryId") categoryId: Long?,
        @Param("term") term: String?,
    ): List<Product>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select product from Product product where product.id = :id")
    fun findByIdForUpdate(id: Long): Product?
}
