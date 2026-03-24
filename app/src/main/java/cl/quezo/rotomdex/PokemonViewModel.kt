package cl.quezo.rotomdex

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PokemonViewModel : ViewModel() {

    var pokemonList = mutableStateOf<List<PokemonListEntry>>(emptyList())
        private set
    var isLoading = mutableStateOf(false)
        private set
    var errorMsg = mutableStateOf("")
        private set

    var pokemonDetails = mutableStateOf<PokemonDetailResponse?>(null)
        private set
    var isDetailLoading = mutableStateOf(false)
        private set

    // --- NUEVAS VARIABLES PARA PAGINACIÓN ---
    private var offset = 0
    private val limit = 20
    var isPaginating = mutableStateOf(false) // Para mostrar la ruedita abajo
        private set
    var endReached = mutableStateOf(false) // Por si llegamos al último Pokémon existente
        private set

    init {
        loadPokemonPaginated()
    }

    // --- NUEVA FUNCIÓN DE PAGINACIÓN ---
    fun loadPokemonPaginated() {
        // Evitamos hacer spam a la API si ya estamos cargando o no hay más datos
        if (isLoading.value || isPaginating.value || endReached.value) return

        viewModelScope.launch {
            try {
                // Si es la primera vez, carga principal. Si no, carga secundaria (paginación)
                if (offset == 0) isLoading.value = true else isPaginating.value = true

                val response = RetrofitClient.apiService.getPokemonList(limit = limit, offset = offset)

                if (response.results.isEmpty()) {
                    endReached.value = true
                } else {
                    offset += limit
                    // Magia aquí: Sumamos la lista vieja con la nueva
                    pokemonList.value = pokemonList.value + response.results
                }
            } catch (e: Exception) {
                errorMsg.value = "Error de conexión: ${e.message}"
            } finally {
                isLoading.value = false
                isPaginating.value = false
            }
        }
    }

    fun fetchPokemonDetail(name: String) {
        viewModelScope.launch {
            try {
                isDetailLoading.value = true
                val response = RetrofitClient.apiService.getPokemonDetail(name.lowercase())
                pokemonDetails.value = response
            } catch (e: Exception) {
                // Silencioso en el MVP
            } finally {
                isDetailLoading.value = false
            }
        }
    }

    fun clearPokemonDetail() {
        pokemonDetails.value = null
    }
}