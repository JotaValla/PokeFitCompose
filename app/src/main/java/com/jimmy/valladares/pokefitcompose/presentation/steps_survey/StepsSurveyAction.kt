package com.jimmy.valladares.pokefitcompose.presentation.steps_survey

sealed interface StepsSurveyAction {
    data class UpdateStepsInput(val input: String) : StepsSurveyAction
    data object ValidateSteps : StepsSurveyAction
    data object ConfirmSteps : StepsSurveyAction
    data object ProceedToNext : StepsSurveyAction
    data object Back : StepsSurveyAction
    data object DismissError : StepsSurveyAction
}
