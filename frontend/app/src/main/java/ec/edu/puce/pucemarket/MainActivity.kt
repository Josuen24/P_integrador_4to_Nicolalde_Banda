package ec.edu.puce.pucemarket

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.puce.pucemarket.models.*
import ec.edu.puce.pucemarket.services.RetrofitClient
import ec.edu.puce.pucemarket.services.TokenStore
import ec.edu.puce.pucemarket.viewmodels.*

private val PucemBlue = Color(0xFF133B67)
private val PucemGold = Color(0xFFFFB71B)
private val SoftBlue = Color(0xFFF2F7FC)
private val WhatsappGreen = Color(0xFF25D366)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = TokenStore(this)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = PucemBlue, secondary = PucemGold, surface = Color.White)) {
                MarketApp(store)
            }
        }
    }
}

@Composable fun MarketApp(store: TokenStore) {
    val vm: MarketViewModel = viewModel(factory = MarketViewModelFactory(RetrofitClient.api(store)))
    var page by remember { mutableStateOf("catalog") }
    var selected by remember { mutableStateOf<Product?>(null) }
    var sessionToken by remember { mutableStateOf(store.token()) }
    val loggedIn = !sessionToken.isNullOrBlank()

    Scaffold(
        containerColor = SoftBlue,
        bottomBar = {
            MarketNavigationBar(
                page = page,
                loggedIn = loggedIn,
                onHome = { page = "catalog" },
                onPublish = { page = if (loggedIn) "publish" else "login" },
                onSales = { page = if (loggedIn) "sales" else "login" },
                onSession = { page = "login" }
            )
        }
    ) { contentPadding ->
        Box(Modifier.padding(contentPadding)) {
            when (page) {
                "login" -> LoginScreen(store, vm, back = { page = "catalog" }) { sessionToken = store.token(); page = "catalog" }
                "publish" -> PublishScreen(vm, back = { page = "catalog" }) { page = "catalog" }
                "sales" -> SellerDashboardScreen(vm, back = { page = "catalog" })
                "detail" -> selected?.let { DetailScreen(it, vm) { page = "catalog" } }
                else -> CatalogScreen(vm, loggedIn, { page = "login" }, { page = "publish" }) { selected = it; page = "detail" }
            }
        }
    }
}

@Composable private fun MarketNavigationBar(page: String, loggedIn: Boolean, onHome: () -> Unit, onPublish: () -> Unit, onSales: () -> Unit, onSession: () -> Unit) {
    NavigationBar {
        NavigationBarItem(selected = page == "catalog" || page == "detail", onClick = onHome, icon = { Text("⌂") }, label = { Text("Inicio") })
        NavigationBarItem(selected = page == "publish", onClick = onPublish, icon = { Text("+") }, label = { Text("Publicar") })
        NavigationBarItem(selected = page == "sales", onClick = onSales, icon = { Text("▣") }, label = { Text("Mis ventas") })
        NavigationBarItem(selected = page == "login", onClick = onSession, icon = { Text("◉") }, label = { Text(if (loggedIn) "Sesión" else "Ingresar") })
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CatalogScreen(vm: MarketViewModel, loggedIn: Boolean, onLogin: () -> Unit, onPublish: () -> Unit, onProduct: (Product) -> Unit) {
    val products by vm.products.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    Scaffold(
        containerColor = SoftBlue,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("PUCE Market", fontWeight = FontWeight.Bold); Text("Compra y vende en tu comunidad", style = MaterialTheme.typography.labelSmall) } },
                actions = { TextButton(onClick = if (loggedIn) onPublish else onLogin) { Text(if (loggedIn) "Vender" else "Ingresar", fontWeight = FontWeight.Bold) } }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(padding), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { HeroCard(products.size, loggedIn, onLogin, onPublish) }
            item { OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("¿Qué estás buscando?") }, placeholder = { Text("Ej.: Calculadora, libros, audífonos") }, leadingIcon = { Text("⌕", style = MaterialTheme.typography.headlineSmall) }, trailingIcon = { TextButton(onClick = { vm.search(query) }) { Text("Buscar") } }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) }
            if (loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            error?.let { message -> item { AssistChip(onClick = vm::refresh, label = { Text("$message · Reintentar") }, colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.errorContainer)) } }
            item { Text(if (query.isBlank()) "Productos disponibles" else "Resultados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            if (!loading && products.isEmpty()) item { EmptyCatalog() }
            items(products, key = { it.id }) { ProductCard(it, onProduct) }
        }
    }
}

@Composable private fun HeroCard(count: Int, loggedIn: Boolean, onLogin: () -> Unit, onPublish: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = PucemBlue), shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(22.dp)) {
            Text("Todo lo que necesitas, cerca de ti", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp)); Text("Explora $count publicaciones de la comunidad PUCE.", color = Color(0xFFD8E8F8))
            Spacer(Modifier.height(18.dp)); Button(onClick = if (loggedIn) onPublish else onLogin, colors = ButtonDefaults.buttonColors(containerColor = PucemGold, contentColor = Color(0xFF2D2100))) { Text(if (loggedIn) "Publicar un producto" else "Iniciar sesión", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable private fun ProductCard(product: Product, click: (Product) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { click(product) }, shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFE3EEF9)), contentAlignment = Alignment.Center) { Text("🛍", style = MaterialTheme.typography.headlineMedium) }
            Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(3.dp)); Text(product.category.name, style = MaterialTheme.typography.labelMedium, color = PucemBlue); Text(product.description, maxLines = 1, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
            Column(horizontalAlignment = Alignment.End) { Text("$ ${"%.2f".format(product.price)}", color = PucemBlue, fontWeight = FontWeight.Bold); AssistChip(onClick = { click(product) }, label = { Text("Ver") }) }
        }
    }
}

@Composable private fun EmptyCatalog() {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("📦", style = MaterialTheme.typography.displaySmall); Text("Aún no hay productos", fontWeight = FontWeight.Bold); Text("Vuelve pronto o publica el primero.", color = Color.Gray) }
    }
}

fun openWhatsAppSeller(context: Context, productName: String) {
    try {
        val message = "Hola! Vengo de PUCE Market y estoy interesado en tu producto: '$productName'."
        val encodedMessage = Uri.encode(message)
        val url = "https://api.whatsapp.com/send?text=$encodedMessage"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
    }
}

@Composable fun DetailScreen(product: Product, vm: MarketViewModel, back: () -> Unit) {
    var price by remember { mutableStateOf(product.price.toString()) }
    var message by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().background(SoftBlue).verticalScroll(rememberScrollState()).padding(20.dp)) {
        TextButton(onClick = back) { Text("← Volver al catálogo") }
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(22.dp)) {
                Box(Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFFE3EEF9)), contentAlignment = Alignment.Center) { Text("🛍", style = MaterialTheme.typography.displayLarge) }
                Spacer(Modifier.height(18.dp))
                AssistChip(onClick = {}, label = { Text(product.category.name) })
                Text(product.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(product.description, color = Color.DarkGray)
                Spacer(Modifier.height(12.dp))
                Text("$ ${"%.2f".format(product.price)}", style = MaterialTheme.typography.headlineSmall, color = PucemBlue, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { openWhatsAppSeller(context, product.name) },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsappGreen),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("💬 Contactar Vendedor por WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Text("¿Te interesa? Envía una oferta formal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        OutlinedTextField(price, { price = it }, label = { Text("Tu oferta ($)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(message, { message = it }, label = { Text("Mensaje para el vendedor") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(14.dp))
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { vm.request(product.id, CreateRequestPayload(price.toDoubleOrNull() ?: 0.0, message)) { result = it ?: "✓ Solicitud enviada correctamente" } },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Enviar solicitud de compra")
        }
        result?.let { Text(it, color = if (it.startsWith("✓")) Color(0xFF177245) else MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp)) }
    }
}

@Composable fun SellerDashboardScreen(vm: MarketViewModel, back: () -> Unit) {
    val products by vm.myProducts.collectAsStateWithLifecycle()
    val requests by vm.receivedRequests.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    var status by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) { vm.loadSellerDashboard { status = it } }
    val productsById = remember(products) { products.associateBy { it.id } }

    Column(Modifier.fillMaxSize().background(SoftBlue).verticalScroll(rememberScrollState()).padding(20.dp)) {
        TextButton(onClick = back) { Text("← Volver al catálogo") }
        Text("Mis ventas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Revisa y responde las ofertas que recibes.", color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        status?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp)) }
        if (!loading && products.isEmpty()) EmptySales()
        requests.filter { it.status == "PENDING" }.forEach { request ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text(productsById[request.productId]?.name ?: "Producto #${request.productId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Oferta de ${request.buyerUsername}", color = PucemBlue)
                    Text("$ ${"%.2f".format(request.offeredPrice)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    request.message?.takeIf { it.isNotBlank() }?.let { Text("“$it”", color = Color.DarkGray) }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = { vm.respondToRequest(request.id, false) { status = it ?: "Oferta rechazada" } }, modifier = Modifier.weight(1f)) { Text("Rechazar") }
                        Button(onClick = { vm.respondToRequest(request.id, true) { status = it ?: "Oferta aceptada" } }, modifier = Modifier.weight(1f)) { Text("Aceptar") }
                    }
                }
            }
        }
        if (!loading && products.isNotEmpty() && requests.none { it.status == "PENDING" }) EmptySales()
    }
}

@Composable private fun EmptySales() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📬", style = MaterialTheme.typography.displaySmall)
            Text("No tienes ofertas pendientes", fontWeight = FontWeight.Bold)
            Text("Las solicitudes de compra de tus productos aparecerán aquí.", color = Color.Gray)
        }
    }
}
@Composable fun PublishScreen(vm: MarketViewModel, back: () -> Unit, done: () -> Unit) {
    val categories by vm.categories.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().background(SoftBlue).verticalScroll(rememberScrollState()).padding(24.dp)) {
        TextButton(onClick = back) { Text("← Cancelar y volver") }
        Text("Publica lo que ya no usas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Tu publicación será visible para toda la comunidad PUCE.", color = Color.Gray)
        Spacer(Modifier.height(20.dp))
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Nombre del producto") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(description, { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                OutlinedTextField(price, { price = it }, label = { Text("Precio ($)") }, modifier = Modifier.fillMaxWidth())
                Text("Categorías disponibles: ${categories.joinToString { "${it.id} · ${it.name}" }}", style = MaterialTheme.typography.labelSmall)
                OutlinedTextField(categoryId, { categoryId = it }, label = { Text("ID de categoría") }, modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = { vm.publish(CreateProductPayload(name, description, price.toDoubleOrNull() ?: 0.0, categoryId.toLongOrNull() ?: 0)) { error -> result = error ?: "Producto publicado"; if (error == null) done() } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Publicar ahora")
                }
                result?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable fun LoginScreen(store: TokenStore, vm: MarketViewModel, back: () -> Unit, done: () -> Unit) {
    var token by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    val loading by vm.loading.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().background(SoftBlue).padding(24.dp), verticalArrangement = Arrangement.Center) {
        Card(shape = RoundedCornerShape(28.dp)) {
            Column(Modifier.padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔐", style = MaterialTheme.typography.displayMedium)
                Text("Bienvenido a PUCE Market", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Ingresa con tu sesión verificada por AWS Cognito.", color = Color.Gray)
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(token, { token = it; status = null }, label = { Text("Access token de Cognito") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), minLines = 3)
                status?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { if (token.isBlank()) status = "Pega tu access_token de Cognito." else vm.validateAndSaveSession(token, store) { error -> if (error == null) done() else status = error } },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp) else Text("Iniciar sesión")
                }
                TextButton(onClick = back) { Text("Volver al catálogo") }
            }
        }
    }
}