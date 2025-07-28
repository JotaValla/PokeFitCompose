package com.jimmy.valladares.pokefitcompose.presentation.steps_survey.providers

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.FitnessGoal
import com.jimmy.valladares.pokefitcompose.presentation.steps_survey.StepsSurveyState

class StepsSurveyPreviewProvider : PreviewParameterProvider<StepsSurveyState> {
    override val values = sequenceOf(
        // Estado inicial - campo vacío
        StepsSurveyState(
            initialGoal = FitnessGoal.EXIT_SEDENTARY
        ),
        
        // Número válido ingresado
        StepsSurveyState(
            initialGoal = FitnessGoal.EXIT_SEDENTARY,
            stepsInputText = "8000",
            dailyStepsGoal = 8000,
            isValidInput = true,
            canProceed = true
        ),
        
        // Número inválido - muy bajo
        StepsSurveyState(
            initialGoal = FitnessGoal.EXIT_SEDENTARY,
            stepsInputText = "500",
            errorMessage = "El número debe estar entre 1000 y 50000 pasos",
            isValidInput = false
        ),
        
        // Número inválido - muy alto
        StepsSurveyState(
            initialGoal = FitnessGoal.EXIT_SEDENTARY,
            stepsInputText = "100000",
            errorMessage = "El número debe estar entre 1000 y 50000 pasos",
            isValidInput = false
        ),
        
        // Estado de loading
        StepsSurveyState(
            initialGoal = FitnessGoal.EXIT_SEDENTARY,
            stepsInputText = "10000",
            dailyStepsGoal = 10000,
            isLoading = true,
            isValidInput = true,
            canProceed = true
        ),
        
        // Estado de error general
        StepsSurveyState(
            initialGoal = FitnessGoal.EXIT_SEDENTARY,
            errorMessage = "Error al procesar tu objetivo"
        ),
        
        // Rango límite inferior válido
        StepsSurveyState(
            initialGoal = FitnessGoal.EXIT_SEDENTARY,
            stepsInputText = "1000",
            dailyStepsGoal = 1000,
            isValidInput = true,
            canProceed = true
        ),
        
        // Rango límite superior válido
        StepsSurveyState(
            initialGoal = FitnessGoal.EXIT_SEDENTARY,
            stepsInputText = "50000",
            dailyStepsGoal = 50000,
            isValidInput = true,
            canProceed = true
        )
    )
}
