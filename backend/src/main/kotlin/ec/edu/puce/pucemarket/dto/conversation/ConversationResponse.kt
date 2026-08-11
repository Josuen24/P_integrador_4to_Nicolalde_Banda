package ec.edu.puce.pucemarket.dto.conversation

import java.time.Instant

data class ConversationResponse(val id: Long, val purchaseRequestId: Long, val productId: Long, val buyerUsername: String, val sellerUsername: String, val createdAt: Instant)
