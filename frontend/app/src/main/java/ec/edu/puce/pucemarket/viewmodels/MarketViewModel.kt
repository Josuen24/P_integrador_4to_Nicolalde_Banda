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
    private val _myProducts = MutableStateFlow<List<Product>>(emptyList())
    val myProducts: StateFlow<List<Product>> = _myProducts.asStateFlow()
    private val _receivedRequests = MutableStateFlow<List<PurchaseRequest>>(emptyList())
    val receivedRequests: StateFlow<List<PurchaseRequest>> = _receivedRequests.asStateFlow()

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
    fun loadSellerDashboard(done: (String?) -> Unit = {}) = viewModelScope.launch {
        _loading.value = true
        try {
            val sellerProducts = api.myProducts()
            _myProducts.value = sellerProducts
            _receivedRequests.value = sellerProducts.flatMap { api.receivedRequests(it.id) }
            done(null)
        } catch (e: Exception) {
            done("No se pudieron cargar tus ofertas. Inicia sesión como vendedor.")
        } finally { _loading.value = false }
    }

    fun respondToRequest(requestId: Long, accept: Boolean, done: (String?) -> Unit) = viewModelScope.launch {
        try {
            if (accept) api.acceptRequest(requestId) else api.rejectRequest(requestId)
            loadSellerDashboard(done)
        } catch (e: Exception) { done("No se pudo actualizar la oferta.") }
    }
    fun validateAndSaveSession(token: String, tokenStore: TokenStore, done: (String?) -> Unit) = viewModelScope.launch {
        _loading.value = true
        try {
            // El interceptor obtiene el JWT desde TokenStore, por eso se guarda antes de validar.
            tokenStore.save(token, emptyList())
            val session = api.session()
            tokenStore.save(token, session.roles)
            done(null)
        } catch (e: Exception) {
            tokenStore.clear()
            done("No se pudo validar la sesión. Verifica que pegaste el access_token completo de Cognito.")
        } finally { _loading.value = false }
    }
}