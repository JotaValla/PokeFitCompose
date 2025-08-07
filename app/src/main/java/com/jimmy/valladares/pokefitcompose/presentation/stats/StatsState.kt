package com.jimmy.valladares.pokefitcompose.presentation.stats

data class StatsState(
    // Información del Pokemon
    val pokemonName: String = "Eevee",
    val selectedPokemon: String = "eevee",
    val currentLevel: Int = 1,
    val currentExp: Int = 0,
    val maxExp: Int = 1000,
    
    // Información del Usuario
    val userName: String = "",
    val userAge: Int = 0,
    val userWeight: Double = 0.0,
    
    // Estadísticas de actividad
    val averageDaysPerWeek: Float = 0f,
    val maxStreak: Int = 0,
    val averageMinutes: Int = 0,
    val totalWorkouts: Int = 0,
    
    // Estadísticas de experiencia
    val weeklyExp: Int = 0,
    val previousWeekExp: Int = 0,
    val totalExp: Int = 0,
    val totalLevelsGained: Int = 0,
    
    // Estado de carga
    val isLoading: Boolean = false
)
