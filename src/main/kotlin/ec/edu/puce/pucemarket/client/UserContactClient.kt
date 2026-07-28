package ec.edu.puce.pucemarket.client

interface UserContactClient {
    fun getPhoneByUsername(username: String): String
}
