package ec.edu.puce.pucemarket.service

import ec.edu.puce.pucemarket.dto.conversation.ConversationResponse
import ec.edu.puce.pucemarket.dto.conversation.CreateMessageRequest
import ec.edu.puce.pucemarket.dto.conversation.MessageResponse

interface ConversationService {
    fun createForPurchaseRequest(requestId: Long): ConversationResponse
    fun getMyConversations(username: String): List<ConversationResponse>
    fun getMessages(conversationId: Long, username: String): List<MessageResponse>
    fun sendMessage(conversationId: Long, request: CreateMessageRequest, username: String): MessageResponse
    fun deleteMessage(messageId: Long, username: String)
}
