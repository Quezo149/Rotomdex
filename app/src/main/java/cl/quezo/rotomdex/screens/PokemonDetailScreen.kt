package cl.quezo.rotomdex.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.quezo.rotomdex.PokemonViewModel
import coil.compose.AsyncImage

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