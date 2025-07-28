package com.jimmy.valladares.pokefitcompose.presentation.initial_survey

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
class InitialSurveyViewModel @Inject constructor() : ViewModel() {
    
    private val _state = MutableStateFlow(InitialSurveyState())
    val state = _state.asStateFlow()
    
    private val _events = Channel<InitialSurveyEvent>()
    val events = _events.receiveAsFlow()
    
    fun onAction(action: InitialSurveyAction) {
        when (action) {
            is InitialSurveyAction.SelectGoal -> {
                selectGoal(action.goal)
            }
            is InitialSurveyAction.ProceedToNext -> {
                proceedToNext()
            }
            is InitialSurveyAction.Back -> {
                navigateBack()
            }
            is InitialSurveyAction.DismissError -> {
                dismissError()
            }
        }
    }
    
    private fun selectGoal(goal: FitnessGoal) {
        _state.update { currentState ->
            currentState.copy(
                selectedGoal = goal,
                canProceed = true,
                error = null
            )
        }
    }
    
    private fun proceedToNext() {
        if (_state.value.selectedGoal == null) {
            _state.update { currentState ->
                currentState.copy(
                    error = "Por favor selecciona un objetivo antes de continuar"
                )
            }
            return
        }
        
        _state.update { currentState ->
            currentState.copy(isLoading = true)
        }
        
        viewModelScope.launch {
            try {
                // Navegación condicional basada en el objetivo seleccionado
                when (_state.value.selectedGoal) {
                    FitnessGoal.IMPROVE_TRAINING -> {
                        // Si seleccionó "Mejorar mi entrenamiento", va a la encuesta de entrenamiento
                        _events.send(InitialSurveyEvent.NavigateToTrainingSurvey)
                    }
                    FitnessGoal.EXIT_SEDENTARY -> {
                        // Si seleccionó "Salir de sedentarismo", va a la encuesta de pasos
                        _events.send(InitialSurveyEvent.NavigateToStepsSurvey)
                    }
                    null -> {
                        // No debería llegar aquí, pero por seguridad
                        throw IllegalStateException("No goal selected")
                    }
                }
                
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
                _events.send(InitialSurveyEvent.ShowError("Error al procesar tu selección"))
            }
        }
    }
    
    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(InitialSurveyEvent.NavigateBack)
        }
    }
    
    private fun dismissError() {
        _state.update { currentState ->
            currentState.copy(error = null)
        }
    }
}
