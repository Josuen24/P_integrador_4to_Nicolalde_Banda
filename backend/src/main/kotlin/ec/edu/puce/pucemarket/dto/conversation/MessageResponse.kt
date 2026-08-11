package ec.edu.puce.pucemarket.dto.conversation

import java.time.Instant

data class MessageResponse(val id: Long, val conversationId: Long, val senderUsername: String, val content: String, val createdAt: Instant)
