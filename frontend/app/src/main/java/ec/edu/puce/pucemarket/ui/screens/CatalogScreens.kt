package ec.edu.puce.pucemarket.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ec.edu.puce.pucemarket.models.CreateRequestPayload
import ec.edu.puce.pucemarket.models.Product
import ec.edu.puce.pucemarket.ui.theme.PuceColors
import ec.edu.puce.pucemarket.viewmodels.MarketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: MarketViewModel,
    loggedIn: Boolean,
    seller: Boolean,
    buyer: Boolean,
    onLogin: () -> Unit,
    onPublish: () -> Unit,
    onRequests: () -> Unit,
    onProduct: (Product) -> Unit,
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    Scaffold(
        containerColor = PuceColors.Background,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PuceColors.Blue, titleContentColor = Color.White),
                title = { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("PUCE Market", fontWeight = FontWeight.Bold); Text("Comunidad universitaria", style = MaterialTheme.typography.labelSmall, color = PuceColors.BlueLight) } },
                actions = { TextButton(onClick = when { seller -> onPublish; buyer -> onRequests; else -> onLogin }) { Text(if (seller) "Vender" else if (buyer) "Mis compras" else "Ingresar", color = PuceColors.Gold, fontWeight = FontWeight.Bold) } },
            )
        },
    ) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { HeroCard(products.size, loggedIn, seller, buyer, onLogin, onPublish, onRequests) }
            item {
                OutlinedTextField(query, { query = it }, label = { Text("¿Qué estás buscando?") }, placeholder = { Text("Ej.: libros, calculadora, audífonos") }, trailingIcon = { TextButton(onClick = { viewModel.search(query) }) { Text("Buscar") } }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
            }
            if (loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            error?.let { item { AssistChip(onClick = viewModel::refresh, label = { Text("$it · Reintentar") }) } }
            item { Text(if (query.isBlank()) "Productos disponibles" else "Resultados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            if (!loading && products.isEmpty()) item { EmptyState("📦", "Aún no hay productos", "Vuelve pronto o publica el primero.") }
            items(products, key = { it.id }) { ProductCard(it, onProduct) }
        }
    }
}

@Composable
private fun HeroCard(count: Int, loggedIn: Boolean, seller: Boolean, buyer: Boolean, onLogin: () -> Unit, onPublish: () -> Unit, onRequests: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = PuceColors.Blue), shape = RoundedCornerShape(28.dp)) {
        Column(Modifier.padding(24.dp)) {
            Text("Comunidad PUCE", color = PuceColors.Gold, style = MaterialTheme.typography.labelLarge)
            Text("Todo lo que necesitas, cerca de ti", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Explora $count publicaciones verificadas de la comunidad.", color = PuceColors.BlueLight)
            Spacer(Modifier.height(16.dp))
            Button(onClick = when { seller -> onPublish; buyer -> onRequests; else -> onLogin }, colors = ButtonDefaults.buttonColors(containerColor = PuceColors.Gold, contentColor = PuceColors.Ink)) { Text(if (seller) "Publicar un producto" else if (buyer) "Ver mis solicitudes" else "Iniciar sesión") }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onProduct: (Product) -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onProduct(product) }, colors = CardDefaults.cardColors(containerColor = PuceColors.Surface), shape = RoundedCornerShape(22.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(58.dp).clip(RoundedCornerShape(16.dp)).background(PuceColors.BlueLight), contentAlignment = Alignment.Center) { Text("🛍") }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(product.name, fontWeight = FontWeight.Bold); Text(product.category.name, color = PuceColors.Blue); Text(product.description, maxLines = 1, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
            Text("$ ${"%.2f".format(product.price)}", color = PuceColors.Blue, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DetailScreen(product: Product, viewModel: MarketViewModel, onBack: () -> Unit) {
    var price by remember { mutableStateOf(product.price.toString()) }
    var message by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().background(PuceColors.Background).verticalScroll(rememberScrollState()).padding(20.dp)) {
        TextButton(onClick = onBack) { Text("← Volver al catálogo") }
        Card(colors = CardDefaults.cardColors(containerColor = PuceColors.Surface), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp)) {
                Text("🛍", style = MaterialTheme.typography.displayMedium)
                AssistChip(onClick = {}, label = { Text(product.category.name) })
                Text(product.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(product.description)
                Text("$ ${"%.2f".format(product.price)}", color = PuceColors.Blue, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { openWhatsApp(context, product.name) }, colors = ButtonDefaults.buttonColors(containerColor = PuceColors.WhatsApp), modifier = Modifier.fillMaxWidth()) { Text("Contactar vendedor por WhatsApp") }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("¿Te interesa? Envía una oferta formal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        OutlinedTextField(price, { price = it }, label = { Text("Tu oferta ($)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(message, { message = it }, label = { Text("Mensaje para el vendedor") }, minLines = 3, modifier = Modifier.fillMaxWidth())
        Button(onClick = { viewModel.request(product.id, CreateRequestPayload(price.toDoubleOrNull() ?: 0.0, message)) { result = it ?: "Solicitud enviada correctamente" } }, modifier = Modifier.fillMaxWidth()) { Text("Enviar solicitud de compra") }
        result?.let { Text(it, color = if (it.contains("correctamente")) Color(0xFF177245) else MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp)) }
    }
}

private fun openWhatsApp(context: Context, productName: String) {
    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode("Hola, estoy interesado en '$productName' en PUCE Market.")}"))) }
    catch (_: Exception) { Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show() }
}

@Composable
fun EmptyState(icon: String, title: String, description: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(icon, style = MaterialTheme.typography.displaySmall); Text(title, fontWeight = FontWeight.Bold); Text(description, color = Color.Gray) } }
}
