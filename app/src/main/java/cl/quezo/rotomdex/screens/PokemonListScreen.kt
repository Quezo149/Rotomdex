package cl.quezo.rotomdex.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
                        // Cambiamos a itemsIndexed para saber qué posición estamos dibujando
                        itemsIndexed(pokemonList) { index, pokemon ->

                            // EL GATILLO: Si estamos dibujando la penúltima tarjeta y no estamos cargando ya...
                            if (index >= pokemonList.size - 1 && !isLoading && !viewModel.isPaginating.value) {
                                LaunchedEffect(key1 = true) {
                                    viewModel.loadPokemonPaginated()
                                }
                            }

                            PokemonCard(
                                name = pokemon.name.replaceFirstChar { it.uppercase() },
                                number = pokemon.number,
                                type = "Pokémon",
                                imageUrl = pokemon.imageUrl,
                                onClick = { navController.navigate("detalle/${pokemon.name}") }
                            )
                        }

                        // Agregamos un indicador visual al fondo de la lista mientras carga los siguientes
                        if (viewModel.isPaginating.value) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color(0xFFE3350D))
                                }
                            }
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
