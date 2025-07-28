package com.jimmy.valladares.pokefitcompose.presentation.steps_survey

sealed interface StepsSurveyEvent {
    data object NavigateToNextStep : StepsSurveyEvent
    data object NavigateBack : StepsSurveyEvent
    data class ShowValidationError(val message: String) : StepsSurveyEvent
    data class ShowError(val message: String) : StepsSurveyEvent
    data object ShowSuccessFeedback : StepsSurveyEvent
}
