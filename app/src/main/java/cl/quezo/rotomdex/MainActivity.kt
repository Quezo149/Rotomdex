package cl.quezo.rotomdex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.quezo.rotomdex.screens.PokemonDetailScreen
import cl.quezo.rotomdex.screens.PokemonListScreen
// Asegúrate de importar tus pantallas si las pusiste en un paquete nuevo
import cl.quezo.rotomdex.ui.theme.RotomdexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RotomdexTheme {
                val viewModel: PokemonViewModel = viewModel()
                RotomdexApp(viewModel)
            }
        }
    }
}

@Composable
fun RotomdexApp(viewModel: PokemonViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lista") {
        composable("lista") {
            PokemonListScreen(viewModel, navController)
        }

        composable("detalle/{pokemonName}") { backStackEntry ->
            val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: "Desconocido"
            PokemonDetailScreen(pokemonName, viewModel, navController)
        }
    }
}