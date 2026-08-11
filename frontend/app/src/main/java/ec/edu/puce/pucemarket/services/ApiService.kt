package ec.edu.puce.pucemarket.services

import ec.edu.puce.pucemarket.models.*
import retrofit2.http.*

interface ApiService {
    @GET("api/categories") suspend fun categories(): List<Category>
    @GET("api/products") suspend fun products(): List<Product>
    @GET("api/products/search") suspend fun search(@Query("categoryId") categoryId: Long?, @Query("query") query: String?): List<Product>
    @GET("api/products/{id}") suspend fun product(@Path("id") id: Long): Product
    @POST("api/products") suspend fun createProduct(@Body body: CreateProductPayload): Product
    @GET("api/products/me") suspend fun myProducts(): List<Product>
    @PUT("api/products/{id}") suspend fun updateProduct(@Path("id") id: Long, @Body body: CreateProductPayload): Product
    @DELETE("api/products/{id}") suspend fun deleteProduct(@Path("id") id: Long)
    @PATCH("api/products/{id}/sold") suspend fun markSold(@Path("id") id: Long): Product
    @POST("api/products/{id}/requests") suspend fun createRequest(@Path("id") id: Long, @Body body: CreateRequestPayload): PurchaseRequest
    @GET("api/purchase-requests/me") suspend fun myRequests(): List<PurchaseRequest>
    @GET("api/products/{id}/requests") suspend fun receivedRequests(@Path("id") id: Long): List<PurchaseRequest>
    @PATCH("api/purchase-requests/{id}/accept") suspend fun acceptRequest(@Path("id") id: Long): PurchaseRequest
    @PATCH("api/purchase-requests/{id}/reject") suspend fun rejectRequest(@Path("id") id: Long): PurchaseRequest
    @DELETE("api/purchase-requests/{id}") suspend fun cancelRequest(@Path("id") id: Long)
    @GET("api/purchase-requests/{id}/buyer-contact") suspend fun buyerContact(@Path("id") id: Long): BuyerContact
    @GET("api/conversations/me") suspend fun conversations(): List<Conversation>
    @GET("api/conversations/{id}/messages") suspend fun messages(@Path("id") id: Long): List<Message>
    @POST("api/conversations/{id}/messages") suspend fun sendMessage(@Path("id") id: Long, @Body body: CreateMessagePayload): Message
}
