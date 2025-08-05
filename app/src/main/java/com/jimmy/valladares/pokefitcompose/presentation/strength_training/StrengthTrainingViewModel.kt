package com.jimmy.valladares.pokefitcompose.presentation.strength_training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StrengthTrainingViewModel @Inject constructor() : ViewModel() {
    
    private val _state = MutableStateFlow(StrengthTrainingState())
    val state: StateFlow<StrengthTrainingState> = _state.asStateFlow()
    
    private val _events = MutableSharedFlow<StrengthTrainingEvent>()
    val events = _events.asSharedFlow()
    
    private var timerJob: Job? = null
    
    fun onAction(action: StrengthTrainingAction) {
        try {
            when (action) {
                is StrengthTrainingAction.SelectExercise -> {
                    selectExercise(action.exercise)
                }
                StrengthTrainingAction.StartTraining -> {
                    startTraining()
                }
                StrengthTrainingAction.PauseResumeTimer -> {
                    pauseResumeTimer()
                }
                StrengthTrainingAction.AddSet -> {
                    addSet()
                }
                is StrengthTrainingAction.ToggleSetComplete -> {
                    toggleSetComplete(action.index)
                }
                is StrengthTrainingAction.UpdateWeight -> {
                    updateWeight(action.index, action.weight)
                }
                is StrengthTrainingAction.UpdateReps -> {
                    updateReps(action.index, action.reps)
                }
                StrengthTrainingAction.FinishTraining -> {
                    finishTraining()
                }
                StrengthTrainingAction.TimerTick -> {
                    timerTick()
                }
                StrengthTrainingAction.SwitchExercise -> {
                    switchExercise()
                }
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _events.emit(StrengthTrainingEvent.ShowMessage("Error: ${e.message}"))
            }
        }
    }
    
    private fun selectExercise(exercise: String) {
        val currentState = _state.value
        
        if (currentState.isTrainingStarted) {
            // Si el entrenamiento ya comenzó, guardar el ejercicio actual antes de cambiar
            saveCurrentExerciseToHistory()
            
            // Cargar las series del nuevo ejercicio si ya existen en el historial
            val exerciseRows = currentState.exerciseHistory[exercise] ?: getInitialRows()
            
            _state.value = currentState.copy(
                selectedExercise = exercise,
                exerciseRows = exerciseRows
            )
        } else {
            // Si el entrenamiento no ha comenzado, simplemente cambiar el ejercicio
            _state.value = currentState.copy(selectedExercise = exercise)
        }
    }
    
    private fun switchExercise() {
        // Esta función se puede usar para cambiar rápidamente entre ejercicios
        saveCurrentExerciseToHistory()
        
        val currentState = _state.value
        val currentIndex = currentState.availableExercises.indexOf(currentState.selectedExercise)
        val nextIndex = (currentIndex + 1) % currentState.availableExercises.size
        val nextExercise = currentState.availableExercises[nextIndex]
        
        selectExercise(nextExercise)
    }
    
    private fun saveCurrentExerciseToHistory() {
        val currentState = _state.value
        val currentExercise = currentState.selectedExercise
        val currentRows = currentState.exerciseRows
        
        // Solo guardar si hay series completadas
        if (currentRows.any { it.isCompleted }) {
            val updatedHistory = currentState.exerciseHistory.toMutableMap()
            updatedHistory[currentExercise] = currentRows
            
            val updatedCompletedExercises = if (!currentState.completedExercises.contains(currentExercise)) {
                currentState.completedExercises + currentExercise
            } else {
                currentState.completedExercises
            }
            
            _state.value = currentState.copy(
                exerciseHistory = updatedHistory,
                completedExercises = updatedCompletedExercises
            )
        }
    }
    
    private fun startTraining() {
        _state.value = _state.value.copy(
            isTrainingStarted = true,
            isPaused = false,
            showPikachuRunning = true
        )
        startTimer()
    }
    
    private fun pauseResumeTimer() {
        val currentState = _state.value
        if (currentState.isPaused) {
            _state.value = currentState.copy(
                isPaused = false,
                showPikachuRunning = true
            )
            startTimer()
        } else {
            _state.value = currentState.copy(
                isPaused = true,
                showPikachuRunning = false
            )
            stopTimer()
        }
    }
    
    private fun addSet() {
        val currentRows = _state.value.exerciseRows
        val newSetNumber = currentRows.size + 1
        val newRow = createNewRow(newSetNumber)
        
        _state.value = _state.value.copy(
            exerciseRows = currentRows + newRow
        )
    }
    
    private fun toggleSetComplete(index: Int) {
        if (index < 0) return
        val currentRows = _state.value.exerciseRows
        if (index >= currentRows.size) return
        
        val updatedRows = currentRows.mapIndexed { i, row ->
            if (i == index) row.copy(isCompleted = !row.isCompleted) else row
        }
        
        val hasCompletedSets = updatedRows.any { it.isCompleted }
        
        _state.value = _state.value.copy(
            exerciseRows = updatedRows,
            isFinishVisible = hasCompletedSets
        )
    }
    
    private fun updateWeight(index: Int, weight: Int) {
        if (index < 0 || weight < 0) return
        val currentRows = _state.value.exerciseRows
        if (index >= currentRows.size) return
        
        val updatedRows = currentRows.mapIndexed { i, row ->
            if (i == index) row.copy(weight = weight) else row
        }
        
        _state.value = _state.value.copy(exerciseRows = updatedRows)
    }
    
    private fun updateReps(index: Int, reps: Int) {
        if (index < 0 || reps < 0) return
        val currentRows = _state.value.exerciseRows
        if (index >= currentRows.size) return
        
        val updatedRows = currentRows.mapIndexed { i, row ->
            if (i == index) row.copy(reps = reps) else row
        }
        
        _state.value = _state.value.copy(exerciseRows = updatedRows)
    }
    
    private fun finishTraining() {
        stopTimer()
        _state.value = StrengthTrainingState() // Reset to initial state
        
        viewModelScope.launch {
            _events.emit(StrengthTrainingEvent.TrainingCompleted)
            _events.emit(StrengthTrainingEvent.ShowMessage("¡Entrenamiento completado!"))
        }
    }
    
    private fun timerTick() {
        val currentState = _state.value
        val newSeconds = currentState.timerSeconds + 1
        val newTimeValue = formatTime(newSeconds)
        
        _state.value = currentState.copy(
            timerSeconds = newSeconds,
            timerValue = newTimeValue
        )
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            try {
                while (!_state.value.isPaused && _state.value.isTrainingStarted) {
                    delay(1000)
                    if (!_state.value.isPaused && _state.value.isTrainingStarted) {
                        timerTick()
                    }
                }
            } catch (e: Exception) {
                // Handle timer error gracefully
                _events.emit(StrengthTrainingEvent.ShowMessage("Timer error: ${e.message}"))
            }
        }
    }
    
    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
    
    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
    
    private fun getInitialRows(): List<ExerciseRow> {
        return listOf(
            ExerciseRow("8 x 12kg", 1, 12, 8),
            ExerciseRow("8 x 12kg", 2, 12, 8),
            ExerciseRow("8 x 12kg", 3, 12, 8)
        )
    }
}
