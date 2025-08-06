package com.jimmy.valladares.pokefitcompose.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jimmy.valladares.pokefitcompose.presentation.strength_training.ExerciseRow
import com.jimmy.valladares.pokefitcompose.presentation.strength_training.StrengthTrainingState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "training_preferences", Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    companion object {
        private const val KEY_TRAINING_STATE = "training_state"
        private const val KEY_IS_TRAINING_ACTIVE = "is_training_active"
        private const val KEY_TIMER_SECONDS = "timer_seconds"
        private const val KEY_IS_PAUSED = "is_paused"
        private const val KEY_SELECTED_EXERCISES = "selected_exercises"
        private const val KEY_CURRENT_EXERCISE_INDEX = "current_exercise_index"
        private const val KEY_EXERCISE_HISTORY = "exercise_history"
        private const val KEY_CURRENT_EXERCISE_ROWS = "current_exercise_rows"
        private const val KEY_COMPLETED_EXERCISES = "completed_exercises"
        private const val KEY_REST_TIMER_SECONDS = "rest_timer_seconds"
        private const val KEY_IS_REST_TIMER_ACTIVE = "is_rest_timer_active"
        private const val KEY_SHOW_REST_TIMER = "show_rest_timer"
        private const val KEY_DEFAULT_REST_TIME = "default_rest_time"
    }
    
    fun saveTrainingState(state: StrengthTrainingState) {
        try {
            val editor = sharedPreferences.edit()
            
            // Guardar campos básicos
            editor.putBoolean(KEY_IS_TRAINING_ACTIVE, state.isTrainingStarted)
            editor.putInt(KEY_TIMER_SECONDS, state.timerSeconds)
            editor.putBoolean(KEY_IS_PAUSED, state.isPaused)
            editor.putInt(KEY_CURRENT_EXERCISE_INDEX, state.currentExerciseIndex)
            
            // Guardar listas como JSON
            editor.putString(KEY_SELECTED_EXERCISES, gson.toJson(state.selectedExercises))
            editor.putString(KEY_COMPLETED_EXERCISES, gson.toJson(state.completedExercises))
            editor.putString(KEY_CURRENT_EXERCISE_ROWS, gson.toJson(state.exerciseRows))
            
            // Guardar historial de ejercicios
            val historyJson = gson.toJson(state.exerciseHistory)
            editor.putString(KEY_EXERCISE_HISTORY, historyJson)
            
            // Guardar estado del temporizador de descanso
            editor.putInt(KEY_REST_TIMER_SECONDS, state.restTimeSeconds)
            editor.putBoolean(KEY_IS_REST_TIMER_ACTIVE, state.isRestTimerActive)
            editor.putBoolean(KEY_SHOW_REST_TIMER, state.showRestTimer)
            editor.putInt(KEY_DEFAULT_REST_TIME, state.defaultRestTime)
            
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun loadTrainingState(): StrengthTrainingState? {
        return try {
            if (!hasActiveTraining()) return null
            
            val selectedExercisesJson = sharedPreferences.getString(KEY_SELECTED_EXERCISES, null)
            val completedExercisesJson = sharedPreferences.getString(KEY_COMPLETED_EXERCISES, null)
            val currentRowsJson = sharedPreferences.getString(KEY_CURRENT_EXERCISE_ROWS, null)
            val historyJson = sharedPreferences.getString(KEY_EXERCISE_HISTORY, null)
            
            val selectedExercises = if (selectedExercisesJson != null) {
                val type = object : TypeToken<List<String>>() {}.type
                gson.fromJson<List<String>>(selectedExercisesJson, type)
            } else emptyList()
            
            val completedExercises = if (completedExercisesJson != null) {
                val type = object : TypeToken<List<String>>() {}.type
                gson.fromJson<List<String>>(completedExercisesJson, type)
            } else emptyList()
            
            val exerciseRows = if (currentRowsJson != null) {
                val type = object : TypeToken<List<ExerciseRow>>() {}.type
                gson.fromJson<List<ExerciseRow>>(currentRowsJson, type)
            } else emptyList()
            
            val exerciseHistory = if (historyJson != null) {
                val type = object : TypeToken<Map<String, List<ExerciseRow>>>() {}.type
                gson.fromJson<Map<String, List<ExerciseRow>>>(historyJson, type)
            } else emptyMap()
            
            // Crear el estado restaurado
            StrengthTrainingState(
                isTrainingStarted = sharedPreferences.getBoolean(KEY_IS_TRAINING_ACTIVE, false),
                timerSeconds = sharedPreferences.getInt(KEY_TIMER_SECONDS, 0),
                timerValue = formatTime(sharedPreferences.getInt(KEY_TIMER_SECONDS, 0)),
                isPaused = sharedPreferences.getBoolean(KEY_IS_PAUSED, true),
                currentExerciseIndex = sharedPreferences.getInt(KEY_CURRENT_EXERCISE_INDEX, 0),
                selectedExercises = selectedExercises,
                completedExercises = completedExercises,
                exerciseRows = exerciseRows,
                exerciseHistory = exerciseHistory,
                restTimeSeconds = sharedPreferences.getInt(KEY_REST_TIMER_SECONDS, 0),
                isRestTimerActive = sharedPreferences.getBoolean(KEY_IS_REST_TIMER_ACTIVE, false),
                showRestTimer = sharedPreferences.getBoolean(KEY_SHOW_REST_TIMER, false),
                defaultRestTime = sharedPreferences.getInt(KEY_DEFAULT_REST_TIME, 90),
                restTimeValue = formatTime(sharedPreferences.getInt(KEY_REST_TIMER_SECONDS, 0)),
                showPikachuRunning = sharedPreferences.getBoolean(KEY_IS_TRAINING_ACTIVE, false) && 
                                  !sharedPreferences.getBoolean(KEY_IS_PAUSED, true),
                isFinishVisible = exerciseRows.any { it.isCompleted }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun hasActiveTraining(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_TRAINING_ACTIVE, false)
    }
    
    fun clearTrainingState() {
        sharedPreferences.edit().clear().apply()
    }
    
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainingSeconds)
    }
}
