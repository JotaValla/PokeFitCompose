package com.jimmy.valladares.pokefitcompose.data.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val name: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val selectedPokemon: String = "",
    val currentLevel: Int = 1,
    val currentExp: Int = 0,
    val totalWorkouts: Int = 0,
    val streakDays: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis()
)

data class PokemonInfo(
    val key: String,
    val name: String,
    val description: String,
    val gifFileName: String
)

object PokemonData {
    val availablePokemons = listOf(
        PokemonInfo(
            key = "torchic",
            name = "Torchic",
            description = "Un Pokémon de tipo Fuego lleno de energía",
            gifFileName = "torchic.gif"
        ),
        PokemonInfo(
            key = "machop",
            name = "Machop",
            description = "Un Pokémon de tipo Lucha muy fuerte",
            gifFileName = "machop.gif"
        ),
        PokemonInfo(
            key = "gible",
            name = "Gible", 
            description = "Un Pokémon de tipo Dragón/Tierra",
            gifFileName = "gible.gif"
        )
    )
}
