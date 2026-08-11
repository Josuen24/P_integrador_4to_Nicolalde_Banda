package ec.edu.puce.pucemarket.service.impl

import ec.edu.puce.pucemarket.dto.conversation.*
import ec.edu.puce.pucemarket.entity.Conversation
import ec.edu.puce.pucemarket.entity.Message
import ec.edu.puce.pucemarket.exception.ForbiddenOperationException
import ec.edu.puce.pucemarket.exception.ResourceNotFoundException
import ec.edu.puce.pucemarket.repository.ConversationRepository
import ec.edu.puce.pucemarket.repository.MessageRepository
import ec.edu.puce.pucemarket.repository.PurchaseRequestRepository
import ec.edu.puce.pucemarket.service.ConversationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConversationServiceImpl(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val purchaseRequestRepository: PurchaseRequestRepository,
) : ConversationService {
    @Transactional
    override fun createForPurchaseRequest(requestId: Long): ConversationResponse {
        conversationRepository.findByPurchaseRequestId(requestId)?.let { return it.toResponse() }
        val request = purchaseRequestRepository.findById(requestId).orElseThrow { ResourceNotFoundException("Oferta $requestId no encontrada") }
        val conversation = Conversation(purchaseRequest = request, buyerUsername = request.buyerUsername, sellerUsername = request.product.sellerUsername)
        return conversationRepository.save(conversation).toResponse()
    }

    @Transactional(readOnly = true)
    override fun getMyConversations(username: String): List<ConversationResponse> =
        conversationRepository.findAllByBuyerUsernameOrSellerUsernameOrderByUpdatedAtDesc(username, username).map { it.toResponse() }

    @Transactional(readOnly = true)
    override fun getMessages(conversationId: Long, username: String): List<MessageResponse> {
        assertParticipant(findConversation(conversationId), username)
        return messageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId).map { it.toResponse() }
    }

    @Transactional
    override fun sendMessage(conversationId: Long, request: CreateMessageRequest, username: String): MessageResponse {
        val conversation = findConversation(conversationId); assertParticipant(conversation, username)
        val message = messageRepository.save(Message(conversation = conversation, senderUsername = username, content = request.content.trim()))
        return message.toResponse()
    }

    @Transactional
    override fun deleteMessage(messageId: Long, username: String) {
        val message = messageRepository.findById(messageId).orElseThrow { ResourceNotFoundException("Mensaje $messageId no encontrado") }
        if (message.senderUsername != username) throw ForbiddenOperationException("Solo el autor puede eliminar este mensaje")
        messageRepository.delete(message)
    }

    private fun findConversation(id: Long) = conversationRepository.findById(id).orElseThrow { ResourceNotFoundException("Conversación $id no encontrada") }
    private fun assertParticipant(conversation: Conversation, username: String) { if (username != conversation.buyerUsername && username != conversation.sellerUsername) throw ForbiddenOperationException("No participas en esta conversación") }
    private fun Conversation.toResponse() = ConversationResponse(requireNotNull(id), requireNotNull(purchaseRequest.id), requireNotNull(purchaseRequest.product.id), buyerUsername, sellerUsername, requireNotNull(createdAt))
    private fun Message.toResponse() = MessageResponse(requireNotNull(id), requireNotNull(conversation.id), senderUsername, content, requireNotNull(createdAt))
}
