package ec.edu.puce.pucemarket.service

import ec.edu.puce.pucemarket.dto.purchaserequest.CreatePurchaseRequest
import ec.edu.puce.pucemarket.dto.purchaserequest.PurchaseRequestResponse

interface PurchaseRequestService {
    fun createRequest(productId: Long, request: CreatePurchaseRequest, buyerUsername: String): PurchaseRequestResponse
    fun getMyRequests(buyerUsername: String): List<PurchaseRequestResponse>
    fun getReceivedRequests(productId: Long, sellerUsername: String): List<PurchaseRequestResponse>
    fun acceptRequest(requestId: Long, sellerUsername: String): PurchaseRequestResponse
    fun rejectRequest(requestId: Long, sellerUsername: String): PurchaseRequestResponse
    fun cancelRequest(requestId: Long, buyerUsername: String)
}
