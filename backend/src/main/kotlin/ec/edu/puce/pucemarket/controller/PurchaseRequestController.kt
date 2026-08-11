package ec.edu.puce.pucemarket.controller

import ec.edu.puce.pucemarket.dto.contact.BuyerContactResponse
import ec.edu.puce.pucemarket.dto.purchaserequest.CreatePurchaseRequest
import ec.edu.puce.pucemarket.dto.purchaserequest.PurchaseRequestResponse
import ec.edu.puce.pucemarket.security.CurrentUser
import ec.edu.puce.pucemarket.service.BuyerContactService
import ec.edu.puce.pucemarket.service.ConversationService
import ec.edu.puce.pucemarket.service.PurchaseRequestService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class PurchaseRequestController(
    private val purchaseRequestService: PurchaseRequestService,
    private val buyerContactService: BuyerContactService,
    private val conversationService: ConversationService,
    private val currentUser: CurrentUser,
) {
    @PostMapping("/api/products/{productId}/requests")
    @PreAuthorize("hasRole('BUYER')")
    fun createRequest(
        @PathVariable productId: Long,
        @Valid @RequestBody request: CreatePurchaseRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<PurchaseRequestResponse> {
        val response = purchaseRequestService.createRequest(productId, request, currentUser.username(jwt))
        conversationService.createForPurchaseRequest(response.id)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/api/purchase-requests/me")
    @PreAuthorize("hasRole('BUYER')")
    fun getMyRequests(@AuthenticationPrincipal jwt: Jwt): List<PurchaseRequestResponse> =
        purchaseRequestService.getMyRequests(currentUser.username(jwt))

    @DeleteMapping("/api/purchase-requests/{requestId}")
    @PreAuthorize("hasRole('BUYER')")
    fun cancelRequest(@PathVariable requestId: Long, @AuthenticationPrincipal jwt: Jwt): ResponseEntity<Void> {
        purchaseRequestService.cancelRequest(requestId, currentUser.username(jwt))
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/api/products/{productId}/requests")
    @PreAuthorize("hasRole('SELLER')")
    fun getReceivedRequests(@PathVariable productId: Long, @AuthenticationPrincipal jwt: Jwt): List<PurchaseRequestResponse> =
        purchaseRequestService.getReceivedRequests(productId, currentUser.username(jwt))

    @PatchMapping("/api/purchase-requests/{requestId}/accept")
    @PreAuthorize("hasRole('SELLER')")
    fun acceptRequest(@PathVariable requestId: Long, @AuthenticationPrincipal jwt: Jwt): PurchaseRequestResponse {
        val response = purchaseRequestService.acceptRequest(requestId, currentUser.username(jwt))
        conversationService.createForPurchaseRequest(requestId)
        return response
    }

    @PatchMapping("/api/purchase-requests/{requestId}/reject")
    @PreAuthorize("hasRole('SELLER')")
    fun rejectRequest(@PathVariable requestId: Long, @AuthenticationPrincipal jwt: Jwt): PurchaseRequestResponse =
        purchaseRequestService.rejectRequest(requestId, currentUser.username(jwt))

    @GetMapping("/api/purchase-requests/{requestId}/buyer-contact")
    @PreAuthorize("hasRole('SELLER')")
    fun getBuyerContact(@PathVariable requestId: Long, @AuthenticationPrincipal jwt: Jwt): BuyerContactResponse =
        buyerContactService.getBuyerContact(requestId, currentUser.username(jwt))
}
