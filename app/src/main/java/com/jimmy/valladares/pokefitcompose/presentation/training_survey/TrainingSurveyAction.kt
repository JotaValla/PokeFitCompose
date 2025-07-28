package com.jimmy.valladares.pokefitcompose.presentation.training_survey

sealed interface TrainingSurveyAction {
    data class SelectTrainingGoal(val goal: TrainingGoal) : TrainingSurveyAction
    data object ProceedToNext : TrainingSurveyAction
    data object Back : TrainingSurveyAction
    data object DismissError : TrainingSurveyAction
}
