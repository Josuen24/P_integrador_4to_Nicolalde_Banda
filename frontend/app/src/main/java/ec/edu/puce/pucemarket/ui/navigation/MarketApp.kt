package ec.edu.puce.pucemarket.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.puce.pucemarket.models.Product
import ec.edu.puce.pucemarket.services.RetrofitClient
import ec.edu.puce.pucemarket.services.TokenStore
import ec.edu.puce.pucemarket.ui.screens.*
import ec.edu.puce.pucemarket.ui.theme.PuceColors
import ec.edu.puce.pucemarket.viewmodels.MarketViewModel
import ec.edu.puce.pucemarket.viewmodels.MarketViewModelFactory

private enum class Destination { CATALOG, DETAIL, LOGIN, PUBLISH, SALES, REQUESTS }

@Composable
fun MarketApp(store: TokenStore) {
    val viewModel: MarketViewModel = viewModel(factory = MarketViewModelFactory(RetrofitClient.api(store)))
    var destination by remember { mutableStateOf(Destination.CATALOG) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var token by remember { mutableStateOf(store.token()) }
    var roles by remember { mutableStateOf(store.roles()) }
    val loggedIn = !token.isNullOrBlank()
    val seller = loggedIn && "SELLER" in roles
    val buyer = loggedIn && "BUYER" in roles

    Scaffold(
        containerColor = PuceColors.Background,
        bottomBar = {
            MarketBottomBar(
                destination = destination,
                loggedIn = loggedIn,
                seller = seller,
                buyer = buyer,
                onNavigate = { destination = it },
            )
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (destination) {
                Destination.CATALOG -> CatalogScreen(
                    viewModel = viewModel,
                    loggedIn = loggedIn,
                    seller = seller,
                    buyer = buyer,
                    onLogin = { destination = Destination.LOGIN },
                    onPublish = { destination = Destination.PUBLISH },
                    onRequests = { destination = Destination.REQUESTS },
                    onProduct = { selectedProduct = it; destination = Destination.DETAIL },
                )
                Destination.DETAIL -> selectedProduct?.let {
                    DetailScreen(it, viewModel) { destination = Destination.CATALOG }
                }
                Destination.LOGIN -> LoginScreen(
                    store = store,
                    viewModel = viewModel,
                    onBack = { destination = Destination.CATALOG },
                    onSessionValidated = { token = store.token(); roles = store.roles(); destination = Destination.CATALOG },
                    onLogout = { token = null; roles = emptySet(); destination = Destination.CATALOG },
                )
                Destination.PUBLISH -> if (seller) PublishScreen(viewModel) { destination = Destination.CATALOG }
                else LaunchedEffect(Unit) { destination = Destination.LOGIN }
                Destination.SALES -> if (seller) SellerDashboardScreen(viewModel) { destination = Destination.CATALOG }
                else LaunchedEffect(Unit) { destination = Destination.LOGIN }
                Destination.REQUESTS -> if (buyer) BuyerRequestsScreen(viewModel) { destination = Destination.CATALOG }
                else LaunchedEffect(Unit) { destination = Destination.LOGIN }
            }
        }
    }
}

@Composable
private fun MarketBottomBar(
    destination: Destination,
    loggedIn: Boolean,
    seller: Boolean,
    buyer: Boolean,
    onNavigate: (Destination) -> Unit,
) {
    val colors = NavigationBarItemDefaults.colors(
        indicatorColor = PuceColors.BlueLight,
        selectedIconColor = PuceColors.Blue,
        selectedTextColor = PuceColors.Blue,
    )
    NavigationBar(containerColor = PuceColors.Surface, tonalElevation = 8.dp) {
        NavigationBarItem(selected = destination == Destination.CATALOG || destination == Destination.DETAIL, onClick = { onNavigate(Destination.CATALOG) }, icon = { Text("⌂") }, label = { Text("Inicio") }, colors = colors)
        if (seller) NavigationBarItem(selected = destination == Destination.PUBLISH, onClick = { onNavigate(Destination.PUBLISH) }, icon = { Text("+") }, label = { Text("Publicar") }, colors = colors)
        if (seller) NavigationBarItem(selected = destination == Destination.SALES, onClick = { onNavigate(Destination.SALES) }, icon = { Text("▣") }, label = { Text("Mis ventas") }, colors = colors)
        if (buyer) NavigationBarItem(selected = destination == Destination.REQUESTS, onClick = { onNavigate(Destination.REQUESTS) }, icon = { Text("◌") }, label = { Text("Mis compras") }, colors = colors)
        NavigationBarItem(selected = destination == Destination.LOGIN, onClick = { onNavigate(Destination.LOGIN) }, icon = { Text("◉") }, label = { Text(if (loggedIn) "Sesión" else "Ingresar") }, colors = colors)
    }
}
