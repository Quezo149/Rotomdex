package cl.quezo.rotomdex

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class PokemonResponse (
    val results: List<PokemonListEntry>
)
data class PokemonListEntry (
    val name: String,
    val url: String

){
    val number: Int
        get() = url.dropLast(1).takeLastWhile {it.isDigit()}.toInt()

    val imageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$number.png"
}

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): PokemonResponse
    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name") name: String
    ): PokemonDetailResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"
    val apiService: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convierte el JSON a las Data Classes de arriba
            .build()
            .create(PokeApiService::class.java)
    }
}

data class PokemonDetailResponse(
    val id: Int,
    val weight: Int,
    val height: Int,
    val types: List<TypeSlot>
)

data class TypeSlot(
    val slot: Int,
    val type: TypeDetail
)

data class TypeDetail(
    val name: String
)