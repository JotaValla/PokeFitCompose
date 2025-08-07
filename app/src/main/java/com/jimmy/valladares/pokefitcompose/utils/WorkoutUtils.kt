package com.jimmy.valladares.pokefitcompose.utils

import android.util.Log
import com.jimmy.valladares.pokefitcompose.data.model.WorkoutExercise
import com.jimmy.valladares.pokefitcompose.data.model.WorkoutSession
import com.jimmy.valladares.pokefitcompose.data.model.WorkoutSet
import com.jimmy.valladares.pokefitcompose.presentation.strength_training.ExerciseRow
import com.jimmy.valladares.pokefitcompose.presentation.strength_training.StrengthTrainingState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WorkoutUtils {
    
    fun createWorkoutSessionFromState(
        userId: String,
        state: StrengthTrainingState
    ): WorkoutSession {
        Log.d("WorkoutUtils", "Creating workout session for user: $userId")
        val exercises = mutableListOf<WorkoutExercise>()
        
        // Procesar el ejercicio actual
        if (state.exerciseRows.isNotEmpty()) {
            val currentExercise = if (state.selectedExercises.isNotEmpty()) {
                state.selectedExercises[state.currentExerciseIndex]
            } else {
                state.selectedExercise
            }
            
            Log.d("WorkoutUtils", "Processing current exercise: $currentExercise with ${state.exerciseRows.size} rows")
            val workoutExercise = createWorkoutExerciseFromRows(currentExercise, state.exerciseRows)
            exercises.add(workoutExercise)
        }
        
        // Procesar ejercicios del historial
        state.exerciseHistory.forEach { (exerciseName, exerciseRows) ->
            // Evitar duplicar el ejercicio actual
            val currentExercise = if (state.selectedExercises.isNotEmpty()) {
                state.selectedExercises[state.currentExerciseIndex]
            } else {
                state.selectedExercise
            }
            
            if (exerciseName != currentExercise && exerciseRows.isNotEmpty()) {
                Log.d("WorkoutUtils", "Processing history exercise: $exerciseName with ${exerciseRows.size} rows")
                val workoutExercise = createWorkoutExerciseFromRows(exerciseName, exerciseRows)
                exercises.add(workoutExercise)
            }
        }
        
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        // Calcular experiencia ganada basada en la duración y número de ejercicios
        val expGained = calculateExperienceGained(exercises, state.timerSeconds)
        
        Log.d("WorkoutUtils", "Created workout with ${exercises.size} exercises, duration: ${state.timerSeconds}s, exp: $expGained")
        
        return WorkoutSession(
            userId = userId,
            exercises = exercises,
            totalDurationSeconds = state.timerSeconds,
            totalDurationFormatted = state.timerValue,
            expGained = expGained,
            completedAt = currentTime,
            date = dateFormat.format(Date(currentTime)),
            workoutType = "strength_training"
        )
    }
    
    private fun createWorkoutExerciseFromRows(
        exerciseName: String,
        exerciseRows: List<ExerciseRow>
    ): WorkoutExercise {
        val workoutSets = exerciseRows.map { row ->
            WorkoutSet(
                setNumber = row.set,
                weight = row.weight,
                reps = row.reps,
                isCompleted = row.isCompleted,
                previous = row.previous
            )
        }
        
        val completedSets = workoutSets.count { it.isCompleted }
        
        return WorkoutExercise(
            name = exerciseName,
            sets = workoutSets,
            totalSets = workoutSets.size,
            completedSets = completedSets
        )
    }
    
    fun formatDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        
        return when {
            hours > 0 -> "%02d:%02d:%02d".format(hours, minutes, remainingSeconds)
            else -> "%02d:%02d".format(minutes, remainingSeconds)
        }
    }
    
    fun isWorkoutValid(state: StrengthTrainingState): Boolean {
        // Verificar que hay al menos una serie completada
        val hasCompletedSets = state.exerciseRows.any { it.isCompleted } ||
                state.exerciseHistory.values.any { rows -> rows.any { it.isCompleted } }
        
        // Verificar que el entrenamiento duró al menos 1 segundo (muy reducido para testing)
        val hasMinimumDuration = state.timerSeconds >= 1 // Reducido a 1 segundo para testing
        
        Log.d("WorkoutUtils", "Validation - hasCompletedSets: $hasCompletedSets, hasMinimumDuration: $hasMinimumDuration (${state.timerSeconds}s)")
        
        return hasCompletedSets && hasMinimumDuration
    }
    
    fun getWorkoutSummaryText(workoutSession: WorkoutSession): String {
        val totalSets = workoutSession.exercises.sumOf { it.completedSets }
        val exerciseNames = workoutSession.exercises.joinToString(", ") { it.name }
        
        return "Entrenamiento completado: $exerciseNames. " +
                "$totalSets series completadas en ${workoutSession.totalDurationFormatted}"
    }
    
    private fun calculateExperienceGained(exercises: List<WorkoutExercise>, durationSeconds: Int): Int {
        // Experiencia base por ejercicio completado
        val exerciseExp = exercises.sumOf { exercise ->
            val completedSets = exercise.sets.count { it.isCompleted }
            completedSets * 10 // 10 EXP por serie completada
        }
        
        // Bonus por duración (1 EXP por minuto de entrenamiento)
        val durationExp = (durationSeconds / 60).coerceAtMost(60) // Máximo 60 EXP por duración
        
        // Bonus por variedad de ejercicios
        val varietyBonus = when (exercises.size) {
            1 -> 0
            2 -> 20
            3 -> 40
            4 -> 60
            else -> 80
        }
        
        val totalExp = exerciseExp + durationExp + varietyBonus
        
        // Mínimo 50 EXP, máximo 200 EXP por entrenamiento
        return totalExp.coerceIn(50, 200)
    }
}
