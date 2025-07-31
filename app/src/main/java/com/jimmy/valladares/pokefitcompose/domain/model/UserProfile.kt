package com.jimmy.valladares.pokefitcompose.domain.model

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val fitnessGoal: String = "", // "training" o "steps"
    val selectedPokemon: String = "eevee", // "charmander", "eevee", "gabite"
    val currentLevel: Int = 1,
    val currentExp: Int = 0,
    val totalWorkouts: Int = 0,
    val streakDays: Int = 0,
    val dailyStepsGoal: Int = 10000,
    val weeklyTrainingGoal: Int = 3,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis()
)
