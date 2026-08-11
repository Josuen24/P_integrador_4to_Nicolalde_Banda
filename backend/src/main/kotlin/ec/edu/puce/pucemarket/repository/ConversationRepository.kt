package ec.edu.puce.pucemarket.repository

import ec.edu.puce.pucemarket.entity.Conversation
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationRepository : JpaRepository<Conversation, Long> {
    fun findByPurchaseRequestId(requestId: Long): Conversation?
    fun findAllByBuyerUsernameOrSellerUsernameOrderByUpdatedAtDesc(buyerUsername: String, sellerUsername: String): List<Conversation>
}
