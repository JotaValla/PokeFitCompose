package com.jimmy.valladares.pokefitcompose.domain.service

import android.util.Log
import com.jimmy.valladares.pokefitcompose.data.model.WorkoutSession
import com.jimmy.valladares.pokefitcompose.data.model.WorkoutExercise
import com.jimmy.valladares.pokefitcompose.data.model.WorkoutSet
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class ExperienceService @Inject constructor() {
    
    companion object {
        private const val TAG = "ExperienceService"
        
        // Constantes de experiencia base
        private const val BASE_WORKOUT_COMPLETION_EXP = 50
        private const val PERFECT_WORKOUT_BONUS = 25 // Todas las series completadas
        private const val DURATION_BONUS_THRESHOLD = 300 // 5 minutos
        private const val DURATION_BONUS_MAX = 20
        
        // Bonificaciones por mejoras
        private const val WEIGHT_IMPROVEMENT_EXP = 5 // Por cada kg más
        private const val REPS_IMPROVEMENT_EXP = 2 // Por cada rep más
        private const val NEW_EXERCISE_EXP = 15 // Por ejercicio nuevo
        private const val CONSISTENCY_BONUS = 10 // Por entrenar días consecutivos
        
        // Multiplicadores
        private const val COMPLETION_RATE_MULTIPLIER = 0.7f // Mínimo 70% de la exp base
    }
    
    data class ExperienceGain(
        val totalExp: Int,
        val baseExp: Int,
        val completionBonus: Int,
        val improvementBonus: Int,
        val durationBonus: Int,
        val consistencyBonus: Int,
        val perfectWorkoutBonus: Int,
        val breakdown: List<String>
    )
    
    /**
     * Calcula la experiencia ganada por un entrenamiento completado
     */
    fun calculateExperienceGain(
        currentWorkout: WorkoutSession,
        previousWorkout: WorkoutSession? = null,
        currentStreak: Int = 0
    ): ExperienceGain {
        Log.d(TAG, "Calculating experience for workout with ${currentWorkout.exercises.size} exercises")
        
        val breakdown = mutableListOf<String>()
        
        // 1. Experiencia base por completar entrenamiento
        val baseExp = BASE_WORKOUT_COMPLETION_EXP
        breakdown.add("Entrenamiento completado: +$baseExp EXP")
        
        // 2. Bonificación por porcentaje de series completadas
        val completionRate = calculateCompletionRate(currentWorkout)
        val completionBonus = (baseExp * (completionRate - COMPLETION_RATE_MULTIPLIER)).toInt()
        if (completionBonus > 0) {
            breakdown.add("Series completadas (${(completionRate * 100).toInt()}%): +$completionBonus EXP")
        }
        
        // 3. Bonificación por entrenamiento perfecto (100% series completadas)
        val perfectWorkoutBonus = if (completionRate >= 1.0f) {
            breakdown.add("¡Entrenamiento perfecto!: +$PERFECT_WORKOUT_BONUS EXP")
            PERFECT_WORKOUT_BONUS
        } else 0
        
        // 4. Bonificación por mejoras respecto al entrenamiento anterior
        val improvementBonus = if (previousWorkout != null) {
            calculateImprovementBonus(currentWorkout, previousWorkout, breakdown)
        } else {
            breakdown.add("Primer entrenamiento registrado: +$NEW_EXERCISE_EXP EXP")
            NEW_EXERCISE_EXP
        }
        
        // 5. Bonificación por duración del entrenamiento
        val durationBonus = calculateDurationBonus(currentWorkout, breakdown)
        
        // 6. Bonificación por consistencia (racha)
        val consistencyBonus = if (currentStreak > 1) {
            val bonus = min(currentStreak * CONSISTENCY_BONUS, 50) // Máximo 50 EXP por consistencia
            breakdown.add("Racha de $currentStreak días: +$bonus EXP")
            bonus
        } else 0
        
        val totalExp = baseExp + completionBonus + improvementBonus + 
                      durationBonus + consistencyBonus + perfectWorkoutBonus
        
        Log.d(TAG, "Total experience calculated: $totalExp EXP")
        
        return ExperienceGain(
            totalExp = totalExp,
            baseExp = baseExp,
            completionBonus = completionBonus,
            improvementBonus = improvementBonus,
            durationBonus = durationBonus,
            consistencyBonus = consistencyBonus,
            perfectWorkoutBonus = perfectWorkoutBonus,
            breakdown = breakdown
        )
    }
    
    /**
     * Calcula el porcentaje de series completadas en el entrenamiento
     */
    private fun calculateCompletionRate(workout: WorkoutSession): Float {
        val totalSets = workout.exercises.sumOf { it.totalSets }
        val completedSets = workout.exercises.sumOf { it.completedSets }
        
        return if (totalSets > 0) {
            completedSets.toFloat() / totalSets.toFloat()
        } else 0f
    }
    
    /**
     * Calcula bonificaciones por mejoras respecto al entrenamiento anterior
     */
    private fun calculateImprovementBonus(
        currentWorkout: WorkoutSession,
        previousWorkout: WorkoutSession,
        breakdown: MutableList<String>
    ): Int {
        var improvementBonus = 0
        var improvementCount = 0
        
        // Comparar ejercicios comunes entre entrenamientos
        currentWorkout.exercises.forEach { currentExercise ->
            val previousExercise = previousWorkout.exercises.find { it.name == currentExercise.name }
            
            if (previousExercise != null) {
                // Ejercicio existente - buscar mejoras
                val exerciseImprovement = compareExercisePerformance(currentExercise, previousExercise)
                improvementBonus += exerciseImprovement
                if (exerciseImprovement > 0) improvementCount++
            } else {
                // Ejercicio nuevo
                improvementBonus += NEW_EXERCISE_EXP
                improvementCount++
            }
        }
        
        if (improvementCount > 0) {
            breakdown.add("Mejoras en $improvementCount ejercicio(s): +$improvementBonus EXP")
        }
        
        return improvementBonus
    }
    
    /**
     * Compara el rendimiento entre dos ejercicios del mismo tipo
     */
    private fun compareExercisePerformance(
        currentExercise: WorkoutExercise,
        previousExercise: WorkoutExercise
    ): Int {
        var bonus = 0
        
        // Comparar series completadas con mayor peso o repeticiones
        currentExercise.sets.forEach { currentSet ->
            if (currentSet.isCompleted) {
                // Buscar la mejor serie anterior completada para comparar
                val bestPreviousSet = previousExercise.sets
                    .filter { it.isCompleted }
                    .maxByOrNull { it.weight * it.reps }
                
                if (bestPreviousSet != null) {
                    val currentTotal = currentSet.weight * currentSet.reps
                    val previousTotal = bestPreviousSet.weight * bestPreviousSet.reps
                    
                    // Bonificar si mejoró el peso total movido
                    if (currentTotal > previousTotal) {
                        val weightDiff = currentSet.weight - bestPreviousSet.weight
                        val repsDiff = currentSet.reps - bestPreviousSet.reps
                        
                        if (weightDiff > 0) bonus += weightDiff * WEIGHT_IMPROVEMENT_EXP
                        if (repsDiff > 0) bonus += repsDiff * REPS_IMPROVEMENT_EXP
                    }
                }
            }
        }
        
        // Bonificar si completó más series que la vez anterior
        val completedSetsDiff = currentExercise.completedSets - previousExercise.completedSets
        if (completedSetsDiff > 0) {
            bonus += completedSetsDiff * 3 // 3 EXP por serie adicional completada
        }
        
        return bonus
    }
    
    /**
     * Calcula bonificación por duración del entrenamiento
     */
    private fun calculateDurationBonus(workout: WorkoutSession, breakdown: MutableList<String>): Int {
        return if (workout.totalDurationSeconds >= DURATION_BONUS_THRESHOLD) {
            val bonus = min(
                (workout.totalDurationSeconds / 60) * 2, // 2 EXP por minuto
                DURATION_BONUS_MAX
            )
            breakdown.add("Entrenamiento de ${workout.totalDurationSeconds / 60} min: +$bonus EXP")
            bonus
        } else 0
    }
    
    /**
     * Calcula el nivel basado en la experiencia total
     */
    fun calculateLevel(totalExp: Int): Int {
        // Fórmula exponencial: cada nivel requiere más experiencia
        // Nivel 1: 0-99 EXP, Nivel 2: 100-249 EXP, Nivel 3: 250-449 EXP, etc.
        return when {
            totalExp < 100 -> 1
            totalExp < 250 -> 2
            totalExp < 450 -> 3
            totalExp < 700 -> 4
            totalExp < 1000 -> 5
            else -> 5 + ((totalExp - 1000) / 400) // Cada 400 EXP después del nivel 5
        }
    }
    
    /**
     * Calcula la experiencia necesaria para el siguiente nivel
     */
    fun getExpForNextLevel(currentLevel: Int): Int {
        return when (currentLevel) {
            1 -> 100
            2 -> 250
            3 -> 450
            4 -> 700
            5 -> 1000
            else -> 1000 + ((currentLevel - 5) * 400)
        }
    }
    
    /**
     * Calcula la experiencia del nivel actual
     */
    fun getExpForCurrentLevel(currentLevel: Int): Int {
        return when (currentLevel) {
            1 -> 0
            2 -> 100
            3 -> 250
            4 -> 450
            5 -> 700
            else -> 1000 + ((currentLevel - 6) * 400)
        }
    }
}
