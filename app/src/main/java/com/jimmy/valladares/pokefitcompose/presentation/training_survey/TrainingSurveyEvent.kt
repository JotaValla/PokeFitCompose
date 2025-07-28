package com.jimmy.valladares.pokefitcompose.presentation.training_survey

sealed interface TrainingSurveyEvent {
    data object NavigateToNextStep : TrainingSurveyEvent
    data object NavigateBack : TrainingSurveyEvent
    data class ShowError(val message: String) : TrainingSurveyEvent
}
