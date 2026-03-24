package cl.quezo.rotomdex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
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
            // Ahora le pasamos el ViewModel a la pantalla de detalles también
            PokemonDetailScreen(pokemonName, viewModel, navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(viewModel: PokemonViewModel, navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Rotomdex", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE3350D))
            )
        }
    ) { innerPadding ->
        val isLoading = viewModel.isLoading.value
        val errorMessage = viewModel.errorMsg.value
        val pokemonList = viewModel.pokemonList.value

        Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                isLoading -> CircularProgressIndicator(color = Color(0xFFE3350D))
                errorMessage.isNotEmpty() -> Text(text = errorMessage, color = Color.Red)
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                        items(pokemonList) { pokemon ->
                            PokemonCard(
                                name = pokemon.name.replaceFirstChar { it.uppercase() },
                                number = pokemon.number,
                                type = "Pokémon",
                                imageUrl = pokemon.imageUrl,
                                onClick = { navController.navigate("detalle/${pokemon.name}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonCard(name: String, number: Int, type: String, imageUrl: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen de $name",
                modifier = Modifier.size(80.dp).background(Color(0xFFF2F2F2), CircleShape).padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "#${number.toString().padStart(3, '0')}", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = type, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

// LA PANTALLA DE DETALLES FINALIZADA
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(pokemonName: String, viewModel: PokemonViewModel, navController: NavController) {

    // LaunchedEffect se ejecuta automáticamente apenas se abre esta pantalla
    LaunchedEffect(pokemonName) {
        viewModel.fetchPokemonDetail(pokemonName)
    }

    val isLoading = viewModel.isDetailLoading.value
    val details = viewModel.pokemonDetails.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pokemonName.replaceFirstChar { it.uppercase() }, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE3350D)),
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearPokemonDetail() // Limpiamos la memoria antes de salir
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFFE3350D))
            } else if (details != null) {
                // Truco para generar la URL de la imagen a partir del ID
                val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${details.id}.png"

                // Formateamos los tipos (la API los da en minúsculas)
                val tiposText = details.types.joinToString(" / ") { it.type.name.replaceFirstChar { char -> char.uppercase() } }

                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = pokemonName,
                        modifier = Modifier.size(200.dp)
                    )

                    Text(text = pokemonName.replaceFirstChar { it.uppercase() }, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text(text = "#${details.id.toString().padStart(3, '0')}", fontSize = 20.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Tipos: $tiposText", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            // La API da el peso en hectogramos y la altura en decímetros. Lo dividimos por 10 para que sea Kg y Metros.
                            Text(text = "Peso: ${details.weight / 10f} kg", fontSize = 16.sp)
                            Text(text = "Altura: ${details.height / 10f} m", fontSize = 16.sp)
                        }
                    }
                }
            } else {
                Text("No se pudo cargar la información", color = Color.Red)
            }
        }
    }
}