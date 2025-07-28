package com.jimmy.valladares.pokefitcompose.presentation.steps_survey

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
class StepsSurveyViewModel @Inject constructor() : ViewModel() {
    
    private val _state = MutableStateFlow(StepsSurveyState())
    val state = _state.asStateFlow()
    
    private val _events = Channel<StepsSurveyEvent>()
    val events = _events.receiveAsFlow()
    
    fun onAction(action: StepsSurveyAction) {
        when (action) {
            is StepsSurveyAction.UpdateStepsInput -> {
                updateStepsInput(action.input)
            }
            is StepsSurveyAction.ValidateSteps -> {
                validateSteps()
            }
            is StepsSurveyAction.ConfirmSteps -> {
                confirmSteps()
            }
            is StepsSurveyAction.ProceedToNext -> {
                proceedToNext()
            }
            is StepsSurveyAction.Back -> {
                navigateBack()
            }
            is StepsSurveyAction.DismissError -> {
                dismissError()
            }
        }
    }
    
    private fun updateStepsInput(input: String) {
        // Solo permitir números
        val filteredInput = input.filter { it.isDigit() }
        
        _state.update { currentState ->
            currentState.copy(
                stepsInputText = filteredInput,
                errorMessage = null
            )
        }
        
        // Validar en tiempo real
        validateStepsInput(filteredInput)
    }
    
    private fun validateStepsInput(input: String) {
        val validationResult = validateStepsRange(input)
        
        _state.update { currentState ->
            currentState.copy(
                isValidInput = validationResult.isValid,
                canProceed = validationResult.isValid,
                dailyStepsGoal = validationResult.steps,
                errorMessage = validationResult.errorMessage
            )
        }
    }
    
    private fun validateSteps() {
        validateStepsInput(_state.value.stepsInputText)
    }
    
    private fun validateStepsRange(input: String): StepsValidationResult {
        if (input.isBlank()) {
            return StepsValidationResult(
                isValid = false,
                errorMessage = null // No mostrar error si está vacío
            )
        }
        
        val steps = input.toIntOrNull()
        if (steps == null) {
            return StepsValidationResult(
                isValid = false,
                errorMessage = "Ingresa un número válido"
            )
        }
        
        if (steps < StepsSurveyState.MIN_STEPS || steps > StepsSurveyState.MAX_STEPS) {
            return StepsValidationResult(
                isValid = false,
                errorMessage = "El número debe estar entre ${StepsSurveyState.MIN_STEPS} y ${StepsSurveyState.MAX_STEPS} pasos"
            )
        }
        
        return StepsValidationResult(
            isValid = true,
            steps = steps
        )
    }
    
    private fun confirmSteps() {
        if (!_state.value.isValidInput || _state.value.dailyStepsGoal == null) {
            viewModelScope.launch {
                _events.send(StepsSurveyEvent.ShowValidationError("Por favor ingresa un número válido de pasos"))
            }
            return
        }
        
        viewModelScope.launch {
            _events.send(StepsSurveyEvent.ShowSuccessFeedback)
            proceedToNext()
        }
    }
    
    private fun proceedToNext() {
        if (_state.value.dailyStepsGoal == null) {
            _state.update { currentState ->
                currentState.copy(
                    errorMessage = "Por favor establece tu objetivo de pasos diarios"
                )
            }
            return
        }
        
        _state.update { currentState ->
            currentState.copy(isLoading = true)
        }
        
        viewModelScope.launch {
            try {
                // Aquí se combinaría el objetivo inicial (EXIT_SEDENTARY) 
                // con el objetivo de pasos diarios establecido
                val userProfile = UserStepsProfile(
                    fitnessGoal = _state.value.initialGoal,
                    dailyStepsGoal = _state.value.dailyStepsGoal!!
                )
                
                // TODO: Guardar el perfil en base de datos local o enviarlo al backend
                
                _events.send(StepsSurveyEvent.NavigateToNextStep)
                
                _state.update { currentState ->
                    currentState.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = "Error al procesar tu objetivo. Inténtalo de nuevo."
                    )
                }
                _events.send(StepsSurveyEvent.ShowError("Error al procesar tu objetivo"))
            }
        }
    }
    
    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(StepsSurveyEvent.NavigateBack)
        }
    }
    
    private fun dismissError() {
        _state.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }
}

// Data class para representar el perfil completo del usuario para sedentarismo
data class UserStepsProfile(
    val fitnessGoal: com.jimmy.valladares.pokefitcompose.presentation.initial_survey.FitnessGoal,
    val dailyStepsGoal: Int
)
