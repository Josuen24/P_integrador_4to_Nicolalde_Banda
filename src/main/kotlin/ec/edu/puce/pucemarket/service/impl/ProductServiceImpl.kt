package ec.edu.puce.pucemarket.service.impl

import ec.edu.puce.pucemarket.dto.product.CreateProductRequest
import ec.edu.puce.pucemarket.dto.product.ProductResponse
import ec.edu.puce.pucemarket.dto.product.UpdateProductRequest
import ec.edu.puce.pucemarket.entity.Category
import ec.edu.puce.pucemarket.entity.Product
import ec.edu.puce.pucemarket.enums.ProductStatus
import ec.edu.puce.pucemarket.exception.ForbiddenOperationException
import ec.edu.puce.pucemarket.exception.InvalidStatusTransitionException
import ec.edu.puce.pucemarket.exception.ResourceNotFoundException
import ec.edu.puce.pucemarket.mapper.ProductMapper
import ec.edu.puce.pucemarket.repository.CategoryRepository
import ec.edu.puce.pucemarket.repository.ProductRepository
import ec.edu.puce.pucemarket.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val productMapper: ProductMapper,
) : ProductService {
    @Transactional(readOnly = true)
    override fun getAvailableProducts(): List<ProductResponse> =
        productRepository.findAllByStatusOrderByCreatedAtDesc(ProductStatus.AVAILABLE).map(productMapper::toResponse)

    @Transactional(readOnly = true)
    override fun searchAvailableProducts(categoryId: Long?, query: String?): List<ProductResponse> =
        productRepository.searchAvailable(categoryId, query?.trim()?.takeIf { it.isNotEmpty() }).map(productMapper::toResponse)

    @Transactional(readOnly = true)
    override fun getProduct(productId: Long): ProductResponse = productMapper.toResponse(findProduct(productId))

    @Transactional(readOnly = true)
    override fun getMyProducts(username: String): List<ProductResponse> =
        productRepository.findAllBySellerUsernameOrderByCreatedAtDesc(username).map(productMapper::toResponse)

    @Transactional
    override fun createProduct(request: CreateProductRequest, sellerUsername: String): ProductResponse {
        val product = productMapper.toEntity(
            name = request.name,
            description = request.description,
            price = requireNotNull(request.price),
            sellerUsername = sellerUsername,
            category = findActiveCategory(requireNotNull(request.categoryId)),
        )
        return productMapper.toResponse(productRepository.save(product))
    }

    @Transactional
    override fun updateProduct(productId: Long, request: UpdateProductRequest, sellerUsername: String): ProductResponse {
        val product = findProduct(productId)
        assertSeller(product, sellerUsername)
        ensureAvailable(product, "Solo se pueden editar productos disponibles")
        product.name = request.name.trim()
        product.description = request.description.trim()
        product.price = requireNotNull(request.price)
        product.category = findActiveCategory(requireNotNull(request.categoryId))
        return productMapper.toResponse(product)
    }

    @Transactional
    override fun deleteProduct(productId: Long, sellerUsername: String) {
        val product = findProduct(productId)
        assertSeller(product, sellerUsername)
        ensureAvailable(product, "No se puede eliminar un producto reservado o vendido")
        productRepository.delete(product)
    }

    @Transactional
    override fun markAsSold(productId: Long, sellerUsername: String): ProductResponse {
        val product = findProduct(productId)
        assertSeller(product, sellerUsername)
        if (product.status != ProductStatus.RESERVED) {
            throw InvalidStatusTransitionException("Solo un producto reservado puede marcarse como vendido")
        }
        product.status = ProductStatus.SOLD
        return productMapper.toResponse(product)
    }

    private fun findProduct(productId: Long): Product = productRepository.findById(productId)
        .orElseThrow { ResourceNotFoundException("Producto $productId no encontrado") }

    private fun findActiveCategory(categoryId: Long): Category = categoryRepository.findById(categoryId)
        .filter { it.active }
        .orElseThrow { ResourceNotFoundException("Categoría activa $categoryId no encontrada") }

    private fun assertSeller(product: Product, username: String) {
        if (product.sellerUsername != username) {
            throw ForbiddenOperationException("No eres propietario de este producto")
        }
    }

    private fun ensureAvailable(product: Product, message: String) {
        if (product.status != ProductStatus.AVAILABLE) {
            throw InvalidStatusTransitionException(message)
        }
    }
}
