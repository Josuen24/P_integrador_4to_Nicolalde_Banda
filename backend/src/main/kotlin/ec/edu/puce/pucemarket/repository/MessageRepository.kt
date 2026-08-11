package ec.edu.puce.pucemarket.repository

import ec.edu.puce.pucemarket.entity.Message
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<Message, Long> {
    fun findAllByConversationIdOrderByCreatedAtAsc(conversationId: Long): List<Message>
}
