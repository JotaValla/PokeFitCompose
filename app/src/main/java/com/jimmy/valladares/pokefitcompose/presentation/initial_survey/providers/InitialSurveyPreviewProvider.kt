package com.jimmy.valladares.pokefitcompose.presentation.initial_survey.providers

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.FitnessGoal
import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.InitialSurveyState

class InitialSurveyPreviewProvider : PreviewParameterProvider<InitialSurveyState> {
    override val values = sequenceOf(
        // Estado inicial - ninguna opción seleccionada
        InitialSurveyState(),
        
        // Primera opción seleccionada
        InitialSurveyState(
            selectedGoal = FitnessGoal.EXIT_SEDENTARY,
            canProceed = true
        ),
        
        // Segunda opción seleccionada
        InitialSurveyState(
            selectedGoal = FitnessGoal.IMPROVE_TRAINING,
            canProceed = true
        ),
        
        // Estado de loading
        InitialSurveyState(
            selectedGoal = FitnessGoal.EXIT_SEDENTARY,
            isLoading = true,
            canProceed = true
        ),
        
        // Estado de error
        InitialSurveyState(
            error = "Error al procesar tu selección"
        )
    )
}
