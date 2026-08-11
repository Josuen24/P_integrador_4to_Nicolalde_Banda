package ec.edu.puce.pucemarket.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "messages")
class Message(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "conversation_id", nullable = false)
    var conversation: Conversation,
    @Column(nullable = false, length = 100) var senderUsername: String,
    @Column(nullable = false, length = 1_000) var content: String,
    @Column(nullable = false, updatable = false) var createdAt: Instant? = null,
) { @PrePersist fun onCreate() { createdAt = Instant.now() } }
