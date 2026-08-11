package ec.edu.puce.pucemarket.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.pucemarket.models.*
import ec.edu.puce.pucemarket.services.ApiService
import ec.edu.puce.pucemarket.services.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketViewModel(private val api: ApiService) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    init { refresh() }
    fun refresh() = run { _loading.value = true; _error.value = null; viewModelScope.launch {
        try { _categories.value = api.categories(); _products.value = api.products() }
        catch (e: Exception) { _error.value = e.message ?: "No se pudo conectar con la API" }
        finally { _loading.value = false }
    }}
    fun search(query: String) = viewModelScope.launch {
        _loading.value = true
        try { _products.value = if (query.isBlank()) api.products() else api.search(null, query) }
        catch (e: Exception) { _error.value = e.message }
        finally { _loading.value = false }
    }
    fun publish(payload: CreateProductPayload, done: (String?) -> Unit) = viewModelScope.launch {
        try { api.createProduct(payload); refresh(); done(null) } catch (e: Exception) { done(e.message ?: "No autorizado") }
    }
    fun request(productId: Long, payload: CreateRequestPayload, done: (String?) -> Unit) = viewModelScope.launch {
        try { api.createRequest(productId, payload); done(null) } catch (e: Exception) { done(e.message ?: "No se pudo enviar la solicitud") }
    }
    fun loadConversations() = viewModelScope.launch { try { _conversations.value = api.conversations() } catch (e: Exception) { _error.value = e.message ?: "No se pudieron cargar los chats" } }
    fun loadMessages(conversationId: Long) = viewModelScope.launch { try { _messages.value = api.messages(conversationId) } catch (e: Exception) { _error.value = e.message ?: "No se pudieron cargar los mensajes" } }
    fun sendChatMessage(conversationId: Long, content: String) = viewModelScope.launch { if (content.isNotBlank()) try { api.sendMessage(conversationId, CreateMessagePayload(content)); loadMessages(conversationId) } catch (e: Exception) { _error.value = e.message ?: "No se pudo enviar el mensaje" } }
    fun validateAndSaveSession(token: String, tokenStore: TokenStore, done: (String?) -> Unit) = viewModelScope.launch {
        _loading.value = true
        try {
            tokenStore.save(token)
            api.conversations()
            done(null)
        } catch (e: Exception) {
            tokenStore.clear()
            done("No se pudo validar la sesión. Usa el access_token completo de Cognito.")
        } finally { _loading.value = false }
    }
}
