package com.jimmy.valladares.pokefitcompose.presentation.initial_survey

sealed interface InitialSurveyEvent {
    data object NavigateToNextStep : InitialSurveyEvent
    data object NavigateToTrainingSurvey : InitialSurveyEvent
    data object NavigateToStepsSurvey : InitialSurveyEvent
    data object NavigateBack : InitialSurveyEvent
    data class ShowError(val message: String) : InitialSurveyEvent
}
