package com.jimmy.valladares.pokefitcompose.data.model

data class UserPokemon(
    val id: String = "", // ID único del Pokémon del usuario
    val pokemonName: String = "", // Nombre del Pokémon (ej: "machop")
    val displayName: String = "", // Nombre mostrado (ej: "Machop")
    val level: Int = 1,
    val currentExp: Int = 0,
    val maxExp: Int = 100, // EXP necesaria para subir al siguiente nivel
    val totalExp: Int = 0, // EXP total acumulada
    val isSelected: Boolean = false, // Si está actualmente seleccionado
    val obtainedAt: Long = System.currentTimeMillis(),
    val lastExpGain: Long = 0L // Último momento que ganó experiencia
) {
    // Calcular el progreso hacia el siguiente nivel
    fun getExpProgress(): Float {
        return if (maxExp > 0) (currentExp.toFloat() / maxExp.toFloat()) else 0f
    }
    
    // Calcular cuánta EXP falta para el siguiente nivel
    fun getExpToNextLevel(): Int {
        return maxExp - currentExp
    }
    
    // Verificar si puede evolucionar (simplificado)
    fun canEvolve(): Boolean {
        return when (pokemonName.lowercase()) {
            "machop" -> level >= 28
            "charmander" -> level >= 16
            "squirtle" -> level >= 16
            "bulbasaur" -> level >= 16
            "pikachu" -> level >= 20 // Con piedra trueno, pero simplificamos
            else -> false
        }
    }
    
    // Obtener el nombre del Pokémon evolucionado
    fun getEvolutionName(): String {
        return when (pokemonName.lowercase()) {
            "machop" -> "machoke"
            "charmander" -> "charmeleon" 
            "squirtle" -> "wartortle"
            "bulbasaur" -> "ivysaur"
            "pikachu" -> "raichu"
            else -> pokemonName
        }
    }
}

data class PokemonLevelUp(
    val previousLevel: Int,
    val newLevel: Int,
    val expGained: Int,
    val totalExp: Int,
    val evolved: Boolean = false,
    val evolutionName: String = ""
)
