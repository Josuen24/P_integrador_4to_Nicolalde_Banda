package ec.edu.puce.pucemarket.services

import android.content.Context

class TokenStore(context: Context) {
    private val preferences = context.getSharedPreferences("puce_market_auth", Context.MODE_PRIVATE)
    fun token(): String? = preferences.getString("access_token", null)
    fun save(token: String) = preferences.edit().putString("access_token", token.trim()).apply()
    fun clear() = preferences.edit().remove("access_token").apply()
}
