package cl.quezo.rotomdex

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PokemonViewModel : ViewModel() {

    var pokemonList = mutableStateOf<List<PokemonListEntry>>(emptyList())
        private set
    var isLoading = mutableStateOf(true)
        private set
    var errorMsg = mutableStateOf("")
        private set

    var pokemonDetails = mutableStateOf<PokemonDetailResponse?>(null)
        private set
    var isDetailLoading = mutableStateOf(false)
        private set

    init {
        fetchPokemon()
    }

    private fun fetchPokemon() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = RetrofitClient.apiService.getPokemonList(limit = 151)
                pokemonList.value = response.results
            } catch (e: Exception) {
                errorMsg.value = "Error de conexión: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // --- NUEVA FUNCIÓN: BUSCAR DETALLES DE UN POKÉMON ---
    fun fetchPokemonDetail(name: String) {
        viewModelScope.launch {
            try {
                isDetailLoading.value = true
                // La API de Pokémon es estricta: exige que los nombres vayan en minúsculas
                val response = RetrofitClient.apiService.getPokemonDetail(name.lowercase())
                pokemonDetails.value = response
            } catch (e: Exception) {
                // Si falla (ej. sin internet), por ahora solo lo atrapamos silenciosamente en el MVP
            } finally {
                isDetailLoading.value = false
            }
        }
    }

    fun clearPokemonDetail() {
        pokemonDetails.value = null
    }
}