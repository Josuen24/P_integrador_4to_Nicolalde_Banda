package ec.edu.puce.pucemarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ec.edu.puce.pucemarket.services.TokenStore
import ec.edu.puce.pucemarket.ui.navigation.MarketApp
import ec.edu.puce.pucemarket.ui.theme.PuceMarketTheme

/** Punto de entrada Android. La UI se organiza en ui/, el estado en viewmodels/ y el acceso HTTP en services/. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PuceMarketTheme {
                MarketApp(TokenStore(this))
            }
        }
    }
}
