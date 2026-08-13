package ec.edu.puce.pucemarket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ec.edu.puce.pucemarket.models.CreateProductPayload
import ec.edu.puce.pucemarket.services.TokenStore
import ec.edu.puce.pucemarket.ui.theme.PuceColors
import ec.edu.puce.pucemarket.viewmodels.MarketViewModel

@Composable
fun PublishScreen(viewModel: MarketViewModel, onBack: () -> Unit) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().background(PuceColors.Background).verticalScroll(rememberScrollState()).padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Cancelar y volver") }
        Text("Publica lo que ya no usas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Tu publicación será visible para la comunidad PUCE.", color = Color.Gray)
        Spacer(Modifier.height(18.dp))
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Nombre del producto") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(description, { description = it }, label = { Text("Descripción") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(price, { price = it }, label = { Text("Precio ($)") }, modifier = Modifier.fillMaxWidth())
                Text("Categorías: ${categories.joinToString { "${it.id} · ${it.name}" }}", style = MaterialTheme.typography.labelSmall)
                OutlinedTextField(categoryId, { categoryId = it }, label = { Text("ID de categoría") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = {
                    if (name.isBlank() || description.isBlank() || (price.toDoubleOrNull() ?: 0.0) <= 0 || (categoryId.toLongOrNull() ?: 0) <= 0) {
                        result = "Completa los campos y usa un precio y categoría válidos."
                    } else viewModel.publish(CreateProductPayload(name, description, price.toDouble(), categoryId.toLong())) { error -> result = error ?: "Producto publicado"; if (error == null) onBack() }
                }, modifier = Modifier.fillMaxWidth()) { Text("Publicar ahora") }
                result?.let { Text(it, color = if (it == "Producto publicado") Color(0xFF177245) else MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
fun BuyerRequestsScreen(viewModel: MarketViewModel, onBack: () -> Unit) {
    val requests by viewModel.myRequests.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    var status by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) { viewModel.loadBuyerRequests { status = it } }
    Column(Modifier.fillMaxSize().background(PuceColors.Background).verticalScroll(rememberScrollState()).padding(20.dp)) {
        TextButton(onClick = onBack) { Text("← Volver al catálogo") }
        Text("Mis compras", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Consulta el estado de las ofertas que enviaste.", color = Color.Gray)
        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        status?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        if (!loading && requests.isEmpty()) EmptyState("🛒", "Aún no tienes solicitudes", "Cuando envíes una oferta, podrás consultarla aquí.")
        requests.forEach { request ->
            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(18.dp)) {
                Text("Solicitud #${request.id}", fontWeight = FontWeight.Bold)
                Text("Producto #${request.productId}", color = PuceColors.Blue)
                Text("Oferta: $ ${"%.2f".format(request.offeredPrice)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                request.message?.takeIf { it.isNotBlank() }?.let { Text("“$it”") }
                AssistChip(onClick = {}, label = { Text("Estado: ${request.status}") })
            } }
        }
    }
}

@Composable
fun SellerDashboardScreen(viewModel: MarketViewModel, onBack: () -> Unit) {
    val products by viewModel.myProducts.collectAsStateWithLifecycle()
    val requests by viewModel.receivedRequests.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    var status by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) { viewModel.loadSellerDashboard { status = it } }
    val productsById = remember(products) { products.associateBy { it.id } }
    Column(Modifier.fillMaxSize().background(PuceColors.Background).verticalScroll(rememberScrollState()).padding(20.dp)) {
        TextButton(onClick = onBack) { Text("← Volver al catálogo") }
        Text("Mis ventas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Revisa y responde las ofertas recibidas.", color = Color.Gray)
        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        status?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        val pending = requests.filter { it.status == "PENDING" }
        if (!loading && pending.isEmpty()) EmptyState("📬", "No tienes ofertas pendientes", "Las solicitudes de compra aparecerán aquí.")
        pending.forEach { request ->
            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(18.dp)) {
                Text(productsById[request.productId]?.name ?: "Producto #${request.productId}", fontWeight = FontWeight.Bold)
                Text("Oferta de ${request.buyerUsername}", color = PuceColors.Blue)
                Text("$ ${"%.2f".format(request.offeredPrice)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                request.message?.takeIf { it.isNotBlank() }?.let { Text("“$it”") }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = { viewModel.respondToRequest(request.id, false) { status = it ?: "Oferta rechazada" } }, modifier = Modifier.weight(1f)) { Text("Rechazar") }
                    Button(onClick = { viewModel.respondToRequest(request.id, true) { status = it ?: "Oferta aceptada" } }, modifier = Modifier.weight(1f)) { Text("Aceptar") }
                }
            } }
        }
    }
}

@Composable
fun LoginScreen(store: TokenStore, viewModel: MarketViewModel, onBack: () -> Unit, onSessionValidated: () -> Unit, onLogout: () -> Unit) {
    var token by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val roles = store.roles()
    val active = !store.token().isNullOrBlank()
    val profile = if ("SELLER" in roles) "Vendedor" else if ("BUYER" in roles) "Comprador" else "Usuario autenticado"
    Column(Modifier.fillMaxSize().background(PuceColors.Background).padding(24.dp), verticalArrangement = Arrangement.Center) {
        Card(shape = RoundedCornerShape(28.dp)) { Column(Modifier.padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (active) "✓" else "🔐", style = MaterialTheme.typography.displayMedium)
            Text(if (active) "Sesión iniciada" else "Bienvenido a PUCE Market", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            if (active) {
                AssistChip(onClick = {}, label = { Text("Perfil activo: $profile") })
                Text(if ("SELLER" in roles) "Puedes publicar productos y administrar ofertas." else "Puedes explorar productos y enviar solicitudes.", color = Color.Gray)
                OutlinedButton(onClick = { store.clear(); onLogout() }, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
                TextButton(onClick = onBack) { Text("Volver al catálogo") }
            } else {
                Text("Ingresa con una sesión verificada por AWS Cognito.", color = Color.Gray)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(token, { token = it; status = null }, label = { Text("Access token de Cognito") }, visualTransformation = PasswordVisualTransformation(), minLines = 3, modifier = Modifier.fillMaxWidth())
                status?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button(onClick = { if (token.isBlank()) status = "Pega tu access_token de Cognito." else viewModel.validateAndSaveSession(token, store) { error -> if (error == null) onSessionValidated() else status = error } }, enabled = !loading, modifier = Modifier.fillMaxWidth()) { if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White) else Text("Iniciar sesión") }
                TextButton(onClick = onBack) { Text("Volver al catálogo") }
            }
        } }
    }
}
