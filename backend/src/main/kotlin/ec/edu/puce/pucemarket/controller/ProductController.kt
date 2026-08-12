package ec.edu.puce.pucemarket.controller

import ec.edu.puce.pucemarket.dto.product.CreateProductRequest
import ec.edu.puce.pucemarket.dto.product.ProductResponse
import ec.edu.puce.pucemarket.dto.product.UpdateProductRequest
import ec.edu.puce.pucemarket.security.CurrentUser
import ec.edu.puce.pucemarket.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService,
    private val currentUser: CurrentUser,
) {
    @GetMapping
    fun getProducts(): List<ProductResponse> = productService.getAvailableProducts()

    @GetMapping("/search")
    fun searchProducts(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) query: String?,
    ): List<ProductResponse> = productService.searchAvailableProducts(categoryId, query)

    @GetMapping("/{productId}")
    fun getProduct(@PathVariable productId: Long): ProductResponse = productService.getProduct(productId)

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getMyProducts(@AuthenticationPrincipal jwt: Jwt): List<ProductResponse> =
        productService.getMyProducts(currentUser.username(jwt))

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createProduct(
        @Valid @RequestBody request: CreateProductRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<ProductResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(productService.createProduct(request, currentUser.username(jwt)))

    @PutMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    fun updateProduct(
        @PathVariable productId: Long,
        @Valid @RequestBody request: UpdateProductRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): ProductResponse = productService.updateProduct(productId, request, currentUser.username(jwt))

    @DeleteMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    fun deleteProduct(@PathVariable productId: Long, @AuthenticationPrincipal jwt: Jwt): ResponseEntity<Void> {
        productService.deleteProduct(productId, currentUser.username(jwt))
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{productId}/sold")
    @PreAuthorize("isAuthenticated()")
    fun markAsSold(@PathVariable productId: Long, @AuthenticationPrincipal jwt: Jwt): ProductResponse =
        productService.markAsSold(productId, currentUser.username(jwt))
}
