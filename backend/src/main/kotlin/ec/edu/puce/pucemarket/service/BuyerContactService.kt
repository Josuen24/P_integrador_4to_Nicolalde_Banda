package ec.edu.puce.pucemarket.service

import ec.edu.puce.pucemarket.dto.contact.BuyerContactResponse

interface BuyerContactService {
    fun getBuyerContact(requestId: Long, sellerUsername: String): BuyerContactResponse
}
