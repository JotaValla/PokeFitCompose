package com.jimmy.valladares.pokefitcompose.presentation.training_survey

import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.FitnessGoal

data class TrainingSurveyState(
    val initialGoal: FitnessGoal = FitnessGoal.IMPROVE_TRAINING,
    val selectedTrainingGoal: TrainingGoal? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val canProceed: Boolean = false
)

enum class TrainingGoal(val displayName: String, val description: String) {
    VELOCITY("Velocidad", "Entrenamientos enfocados en rapidez y agilidad"),
    STRENGTH("Fuerza", "Entrenamientos de potencia y desarrollo muscular"),
    RESISTANCE("Resistencia", "Entrenamientos de cardio y resistencia cardiovascular")
}
