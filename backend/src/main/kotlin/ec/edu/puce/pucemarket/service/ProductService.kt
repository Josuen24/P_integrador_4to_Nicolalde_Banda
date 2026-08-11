package ec.edu.puce.pucemarket.service

import ec.edu.puce.pucemarket.dto.product.CreateProductRequest
import ec.edu.puce.pucemarket.dto.product.ProductResponse
import ec.edu.puce.pucemarket.dto.product.UpdateProductRequest

interface ProductService {
    fun getAvailableProducts(): List<ProductResponse>
    fun searchAvailableProducts(categoryId: Long?, query: String?): List<ProductResponse>
    fun getProduct(productId: Long): ProductResponse
    fun getMyProducts(username: String): List<ProductResponse>
    fun createProduct(request: CreateProductRequest, sellerUsername: String): ProductResponse
    fun updateProduct(productId: Long, request: UpdateProductRequest, sellerUsername: String): ProductResponse
    fun deleteProduct(productId: Long, sellerUsername: String)
    fun markAsSold(productId: Long, sellerUsername: String): ProductResponse
}
