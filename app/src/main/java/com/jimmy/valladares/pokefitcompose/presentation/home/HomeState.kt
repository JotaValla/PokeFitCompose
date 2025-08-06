package com.jimmy.valladares.pokefitcompose.presentation.home

import com.jimmy.valladares.pokefitcompose.data.model.UserProfile

data class HomeState(
    val userProfile: UserProfile? = null,
    val selectedPokemon: String = "eevee", // Pokemon seleccionado durante onboarding
    val pokemonName: String = "Eevee",
    val currentLevel: Int = 1,
    val currentExp: Int = 0,
    val maxExp: Int = 100,
    val streakDays: Int = 0,
    val weeklyProgress: List<DayProgress> = emptyList(),
    val todayTrainings: Int = 0,
    val expToNextLevel: Int = 100,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DayProgress(
    val dayName: String, // "Lun", "Mar", etc.
    val isCompleted: Boolean,
    val expGained: Int = 0
)

// Información rápida para las cards de estadísticas
data class QuickStats(
    val trainingsToday: Int = 0,
    val currentStreak: Int = 0,
    val expToNextLevel: Int = 100,
    val totalWorkouts: Int = 0
)
