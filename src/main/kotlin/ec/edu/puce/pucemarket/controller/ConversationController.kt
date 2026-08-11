package ec.edu.puce.pucemarket.controller

import ec.edu.puce.pucemarket.dto.conversation.*
import ec.edu.puce.pucemarket.security.CurrentUser
import ec.edu.puce.pucemarket.service.ConversationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ConversationController(private val conversationService: ConversationService, private val currentUser: CurrentUser) {
    @GetMapping("/conversations/me") fun myConversations(@AuthenticationPrincipal jwt: Jwt): List<ConversationResponse> = conversationService.getMyConversations(currentUser.username(jwt))
    @GetMapping("/conversations/{conversationId}/messages") fun messages(@PathVariable conversationId: Long, @AuthenticationPrincipal jwt: Jwt): List<MessageResponse> = conversationService.getMessages(conversationId, currentUser.username(jwt))
    @PostMapping("/conversations/{conversationId}/messages") fun send(@PathVariable conversationId: Long, @Valid @RequestBody request: CreateMessageRequest, @AuthenticationPrincipal jwt: Jwt): ResponseEntity<MessageResponse> = ResponseEntity.status(HttpStatus.CREATED).body(conversationService.sendMessage(conversationId, request, currentUser.username(jwt)))
    @DeleteMapping("/messages/{messageId}") fun delete(@PathVariable messageId: Long, @AuthenticationPrincipal jwt: Jwt): ResponseEntity<Void> { conversationService.deleteMessage(messageId, currentUser.username(jwt)); return ResponseEntity.noContent().build() }
}
