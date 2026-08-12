package ec.edu.puce.pucemarket.models

data class Category(
    val id: Long,
    val name: String,
    val description: String
)

data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val status: String,
    val sellerUsername: String,
    val category: Category
)

data class CreateProductPayload(
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: Long
)

data class CreateRequestPayload(
    val offeredPrice: Double,
    val message: String?
)

data class PurchaseRequest(
    val id: Long,
    val productId: Long,
    val offeredPrice: Double,
    val message: String?,
    val status: String,
    val buyerUsername: String
)

data class BuyerContact(
    val buyerUsername: String,
    val whatsappUrl: String
)