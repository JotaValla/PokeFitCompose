package com.jimmy.valladares.pokefitcompose.data.model

data class WorkoutSession(
    val id: String = "",
    val userId: String = "",
    val exercises: List<WorkoutExercise> = emptyList(),
    val totalDurationSeconds: Int = 0,
    val totalDurationFormatted: String = "",
    val expGained: Int = 0, // Experiencia ganada en este workout
    val completedAt: Long = System.currentTimeMillis(),
    val date: String = "", // Fecha en formato "YYYY-MM-DD" para consultas
    val workoutType: String = "strength_training" // Por ahora solo entrenamiento de fuerza
)

data class WorkoutExercise(
    val name: String = "",
    val sets: List<WorkoutSet> = emptyList(),
    val totalSets: Int = 0,
    val completedSets: Int = 0
)

data class WorkoutSet(
    val setNumber: Int = 0,
    val weight: Int = 0,
    val reps: Int = 0,
    val isCompleted: Boolean = false,
    val previous: String = "" // Información del set anterior para referencia
)

// Modelo para resumen de entrenamientos (para estadísticas)
data class WorkoutSummary(
    val userId: String = "",
    val totalWorkouts: Int = 0,
    val totalExercises: Int = 0,
    val totalSets: Int = 0,
    val totalTimeSeconds: Int = 0,
    val lastWorkoutDate: Long = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val favoriteExercises: Map<String, Int> = emptyMap(), // Ejercicio -> cantidad de veces realizado
    val updatedAt: Long = System.currentTimeMillis()
)

// Modelo para estadísticas específicas de la pantalla Stats
data class WorkoutStats(
    val averageDaysPerWeek: Float = 0f,
    val maxStreak: Int = 0,
    val averageMinutes: Int = 0,
    val totalWorkouts: Int = 0,
    val weeklyExp: Int = 0,
    val previousWeekExp: Int = 0
)
