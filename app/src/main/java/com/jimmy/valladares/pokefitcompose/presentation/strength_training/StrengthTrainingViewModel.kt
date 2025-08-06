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
    private var restTimerJob: Job? = null
    
    fun onAction(action: StrengthTrainingAction) {
        try {
            when (action) {
                is StrengthTrainingAction.SelectExercise -> {
                    selectExercise(action.exercise)
                }
                StrengthTrainingAction.AddExerciseToWorkout -> {
                    addExerciseToWorkout()
                }
                is StrengthTrainingAction.RemoveExerciseFromWorkout -> {
                    removeExerciseFromWorkout(action.exercise)
                }
                is StrengthTrainingAction.ChangeCurrentExercise -> {
                    changeCurrentExercise(action.exerciseIndex)
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
                StrengthTrainingAction.StartRestTimer -> {
                    startRestTimer()
                }
                StrengthTrainingAction.RestTimerTick -> {
                    restTimerTick()
                }
                StrengthTrainingAction.SkipRestTimer -> {
                    skipRestTimer()
                }
                StrengthTrainingAction.AddRestTime -> {
                    addRestTime()
                }
                StrengthTrainingAction.SubtractRestTime -> {
                    subtractRestTime()
                }
                is StrengthTrainingAction.SetRestTime -> {
                    setRestTime(action.seconds)
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
        _state.value = currentState.copy(selectedExercise = exercise)
    }
    
    private fun addExerciseToWorkout() {
        val currentState = _state.value
        val selectedExercise = currentState.selectedExercise
        
        if (!currentState.selectedExercises.contains(selectedExercise)) {
            _state.value = currentState.copy(
                selectedExercises = currentState.selectedExercises + selectedExercise
            )
        }
    }
    
    private fun removeExerciseFromWorkout(exercise: String) {
        val currentState = _state.value
        _state.value = currentState.copy(
            selectedExercises = currentState.selectedExercises.filter { it != exercise }
        )
    }
    
    private fun changeCurrentExercise(exerciseIndex: Int) {
        val currentState = _state.value
        if (exerciseIndex >= 0 && exerciseIndex < currentState.selectedExercises.size) {
            // Guardar el progreso del ejercicio actual
            saveCurrentExerciseToHistory()
            
            // Cambiar al nuevo ejercicio
            val newExercise = currentState.selectedExercises[exerciseIndex]
            val exerciseRows = currentState.exerciseHistory[newExercise] ?: getInitialRows()
            
            _state.value = currentState.copy(
                currentExerciseIndex = exerciseIndex,
                exerciseRows = exerciseRows
            )
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
        val currentExercise = if (currentState.selectedExercises.isNotEmpty()) {
            currentState.selectedExercises[currentState.currentExerciseIndex]
        } else {
            currentState.selectedExercise
        }
        val currentRows = currentState.exerciseRows
        
        // Siempre guardar el progreso actual
        val updatedHistory = currentState.exerciseHistory.toMutableMap()
        updatedHistory[currentExercise] = currentRows
        
        // Actualizar la lista de ejercicios completados solo si hay series completadas
        val updatedCompletedExercises = if (currentRows.any { it.isCompleted } && 
            !currentState.completedExercises.contains(currentExercise)) {
            currentState.completedExercises + currentExercise
        } else {
            currentState.completedExercises
        }
        
        _state.value = currentState.copy(
            exerciseHistory = updatedHistory,
            completedExercises = updatedCompletedExercises
        )
    }
    
    private fun startTraining() {
        val currentState = _state.value
        
        // Si no hay ejercicios seleccionados, usar el ejercicio actual
        val exercisesToTrain = if (currentState.selectedExercises.isEmpty()) {
            listOf(currentState.selectedExercise)
        } else {
            currentState.selectedExercises
        }
        
        _state.value = currentState.copy(
            isTrainingStarted = true,
            isPaused = false,
            showPikachuRunning = true,
            selectedExercises = exercisesToTrain,
            currentExerciseIndex = 0
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
        
        // Guardar inmediatamente el progreso actualizado
        saveCurrentExerciseToHistory()
        
        // Si se marca una serie como completada, iniciar o reiniciar el temporizador de descanso
        val toggledRow = updatedRows[index]
        if (toggledRow.isCompleted) {
            // Si no hay temporizador activo o el tiempo ya se acabó, iniciar uno nuevo
            if (!_state.value.isRestTimerActive || _state.value.restTimeSeconds <= 0) {
                startRestTimer()
            }
        }
    }
    
    private fun updateWeight(index: Int, weight: Int) {
        if (index < 0 || weight < 0) return
        val currentRows = _state.value.exerciseRows
        if (index >= currentRows.size) return
        
        val updatedRows = currentRows.mapIndexed { i, row ->
            if (i == index) row.copy(weight = weight) else row
        }
        
        _state.value = _state.value.copy(exerciseRows = updatedRows)
        
        // Guardar inmediatamente el progreso actualizado
        saveCurrentExerciseToHistory()
    }
    
    private fun updateReps(index: Int, reps: Int) {
        if (index < 0 || reps < 0) return
        val currentRows = _state.value.exerciseRows
        if (index >= currentRows.size) return
        
        val updatedRows = currentRows.mapIndexed { i, row ->
            if (i == index) row.copy(reps = reps) else row
        }
        
        _state.value = _state.value.copy(exerciseRows = updatedRows)
        
        // Guardar inmediatamente el progreso actualizado
        saveCurrentExerciseToHistory()
    }
    
    private fun finishTraining() {
        stopTimer()
        stopRestTimer()
        _state.value = StrengthTrainingState() // Reset to initial state
        
        viewModelScope.launch {
            _events.emit(StrengthTrainingEvent.TrainingCompleted)
            _events.emit(StrengthTrainingEvent.ShowMessage("¡Entrenamiento completado!"))
        }
    }
    
    // Funciones para el temporizador de descanso
    private fun startRestTimer() {
        val currentState = _state.value
        
        // Solo reiniciar si el temporizador no está activo o si ya terminó
        if (!currentState.isRestTimerActive || currentState.restTimeSeconds <= 0) {
            _state.value = currentState.copy(
                isRestTimerActive = true,
                restTimeSeconds = currentState.defaultRestTime,
                restTimeValue = formatTime(currentState.defaultRestTime),
                showRestTimer = true
            )
            
            restTimerJob?.cancel()
            restTimerJob = viewModelScope.launch {
                try {
                    while (_state.value.isRestTimerActive && _state.value.restTimeSeconds > 0) {
                        delay(1000)
                        if (_state.value.isRestTimerActive) {
                            restTimerTick()
                        }
                    }
                    // Timer completado
                    if (_state.value.isRestTimerActive && _state.value.restTimeSeconds <= 0) {
                        _state.value = _state.value.copy(
                            isRestTimerActive = false,
                            showRestTimer = false
                        )
                        _events.emit(StrengthTrainingEvent.ShowMessage("¡Descanso completado!"))
                    }
                } catch (e: Exception) {
                    _events.emit(StrengthTrainingEvent.ShowMessage("Error en temporizador: ${e.message}"))
                }
            }
        } else {
            // Si el temporizador ya está activo, solo asegurar que se muestre
            _state.value = currentState.copy(showRestTimer = true)
        }
    }
    
    private fun restTimerTick() {
        val currentState = _state.value
        val newSeconds = currentState.restTimeSeconds - 1
        val newTimeValue = formatTime(maxOf(0, newSeconds))
        
        _state.value = currentState.copy(
            restTimeSeconds = newSeconds,
            restTimeValue = newTimeValue
        )
        
        if (newSeconds <= 0) {
            _state.value = currentState.copy(
                isRestTimerActive = false,
                showRestTimer = false
            )
        }
    }
    
    private fun skipRestTimer() {
        stopRestTimer()
        _state.value = _state.value.copy(
            isRestTimerActive = false,
            showRestTimer = false,
            restTimeSeconds = 0
        )
    }
    
    private fun addRestTime() {
        val currentState = _state.value
        val newSeconds = currentState.restTimeSeconds + 30
        _state.value = currentState.copy(
            restTimeSeconds = newSeconds,
            restTimeValue = formatTime(newSeconds)
        )
    }
    
    private fun subtractRestTime() {
        val currentState = _state.value
        val newSeconds = maxOf(0, currentState.restTimeSeconds - 30)
        _state.value = currentState.copy(
            restTimeSeconds = newSeconds,
            restTimeValue = formatTime(newSeconds)
        )
    }
    
    private fun setRestTime(seconds: Int) {
        _state.value = _state.value.copy(defaultRestTime = seconds)
    }
    
    private fun stopRestTimer() {
        restTimerJob?.cancel()
        restTimerJob = null
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
        stopRestTimer()
    }
    
    private fun getInitialRows(): List<ExerciseRow> {
        return listOf(
            ExerciseRow("8 x 12kg", 1, 12, 8),
            ExerciseRow("8 x 12kg", 2, 12, 8),
            ExerciseRow("8 x 12kg", 3, 12, 8)
        )
    }
}
