package com.jimmy.valladares.pokefitcompose.presentation.initial_survey

data class InitialSurveyState(
    val selectedGoal: FitnessGoal? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val canProceed: Boolean = false
)

enum class FitnessGoal(val displayName: String) {
    EXIT_SEDENTARY("Salir de sedentarismo"),
    IMPROVE_TRAINING("Mejorar mi entrenamiento")
}
