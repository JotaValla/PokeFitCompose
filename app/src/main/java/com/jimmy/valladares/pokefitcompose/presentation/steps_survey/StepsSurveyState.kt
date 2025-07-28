package com.jimmy.valladares.pokefitcompose.presentation.steps_survey

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.FitnessGoal

data class StepsSurveyState(
    val initialGoal: FitnessGoal = FitnessGoal.EXIT_SEDENTARY,
    val stepsInputText: String = "",
    val dailyStepsGoal: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isValidInput: Boolean = false,
    val canProceed: Boolean = false
) {
    companion object {
        const val MIN_STEPS = 1000
        const val MAX_STEPS = 50000
    }
}

data class StepsValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val steps: Int? = null
)
