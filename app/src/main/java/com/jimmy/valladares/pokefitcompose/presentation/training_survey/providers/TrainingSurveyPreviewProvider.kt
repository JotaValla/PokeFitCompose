package com.jimmy.valladares.pokefitcompose.presentation.training_survey.providers

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.FitnessGoal
import com.jimmy.valladares.pokefitcompose.presentation.training_survey.TrainingGoal
import com.jimmy.valladares.pokefitcompose.presentation.training_survey.TrainingSurveyState

class TrainingSurveyPreviewProvider : PreviewParameterProvider<TrainingSurveyState> {
    override val values = sequenceOf(
        // Estado inicial - ninguna opción seleccionada
        TrainingSurveyState(
            initialGoal = FitnessGoal.IMPROVE_TRAINING
        ),
        
        // Velocidad seleccionada
        TrainingSurveyState(
            initialGoal = FitnessGoal.IMPROVE_TRAINING,
            selectedTrainingGoal = TrainingGoal.VELOCITY,
            canProceed = true
        ),
        
        // Fuerza seleccionada
        TrainingSurveyState(
            initialGoal = FitnessGoal.IMPROVE_TRAINING,
            selectedTrainingGoal = TrainingGoal.STRENGTH,
            canProceed = true
        ),
        
        // Resistencia seleccionada
        TrainingSurveyState(
            initialGoal = FitnessGoal.IMPROVE_TRAINING,
            selectedTrainingGoal = TrainingGoal.RESISTANCE,
            canProceed = true
        ),
        
        // Estado de loading
        TrainingSurveyState(
            initialGoal = FitnessGoal.IMPROVE_TRAINING,
            selectedTrainingGoal = TrainingGoal.VELOCITY,
            isLoading = true,
            canProceed = true
        ),
        
        // Estado de error
        TrainingSurveyState(
            initialGoal = FitnessGoal.IMPROVE_TRAINING,
            error = "Error al procesar tu selección"
        )
    )
}
