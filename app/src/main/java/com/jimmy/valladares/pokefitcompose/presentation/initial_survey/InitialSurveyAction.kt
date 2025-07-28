package com.jimmy.valladares.pokefitcompose.presentation.initial_survey

sealed interface InitialSurveyAction {
    data class SelectGoal(val goal: FitnessGoal) : InitialSurveyAction
    data object ProceedToNext : InitialSurveyAction
    data object Back : InitialSurveyAction
    data object DismissError : InitialSurveyAction
}
