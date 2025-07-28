package com.jimmy.valladares.pokefitcompose.presentation.training_survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingSurveyViewModel @Inject constructor() : ViewModel() {
    
    private val _state = MutableStateFlow(TrainingSurveyState())
    val state = _state.asStateFlow()
    
    private val _events = Channel<TrainingSurveyEvent>()
    val events = _events.receiveAsFlow()
    
    fun onAction(action: TrainingSurveyAction) {
        when (action) {
            is TrainingSurveyAction.SelectTrainingGoal -> {
                selectTrainingGoal(action.goal)
            }
            is TrainingSurveyAction.ProceedToNext -> {
                proceedToNext()
            }
            is TrainingSurveyAction.Back -> {
                navigateBack()
            }
            is TrainingSurveyAction.DismissError -> {
                dismissError()
            }
        }
    }
    
    private fun selectTrainingGoal(goal: TrainingGoal) {
        _state.update { currentState ->
            currentState.copy(
                selectedTrainingGoal = goal,
                canProceed = true,
                error = null
            )
        }
    }
    
    private fun proceedToNext() {
        if (_state.value.selectedTrainingGoal == null) {
            _state.update { currentState ->
                currentState.copy(
                    error = "Por favor selecciona un tipo de entrenamiento antes de continuar"
                )
            }
            return
        }
        
        _state.update { currentState ->
            currentState.copy(isLoading = true)
        }
        
        viewModelScope.launch {
            try {
                // Aquí se combinaría el objetivo inicial (IMPROVE_TRAINING) 
                // con el objetivo de entrenamiento específico seleccionado
                // y se guardaría el perfil completo del usuario
                
                val userProfile = UserTrainingProfile(
                    fitnessGoal = _state.value.initialGoal,
                    trainingGoal = _state.value.selectedTrainingGoal!!
                )
                
                // TODO: Guardar el perfil en base de datos local o enviarlo al backend
                
                _events.send(TrainingSurveyEvent.NavigateToNextStep)
                
                _state.update { currentState ->
                    currentState.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Error al procesar tu selección. Inténtalo de nuevo."
                    )
                }
                _events.send(TrainingSurveyEvent.ShowError("Error al procesar tu selección"))
            }
        }
    }
    
    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(TrainingSurveyEvent.NavigateBack)
        }
    }
    
    private fun dismissError() {
        _state.update { currentState ->
            currentState.copy(error = null)
        }
    }
}

// Data class para representar el perfil completo del usuario
data class UserTrainingProfile(
    val fitnessGoal: com.jimmy.valladares.pokefitcompose.presentation.initial_survey.FitnessGoal,
    val trainingGoal: TrainingGoal
)
