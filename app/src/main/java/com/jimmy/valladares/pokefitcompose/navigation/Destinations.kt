package com.jimmy.valladares.pokefitcompose.navigation

import kotlinx.serialization.Serializable

sealed interface Destinations

@Serializable
data object WelcomeDestination : Destinations

@Serializable
data object LoginDestination : Destinations

@Serializable
data object RegisterDestination : Destinations

@Serializable
data object InitialSurveyDestination : Destinations

@Serializable
data object TrainingSurveyDestination : Destinations

@Serializable
data object StepsSurveyDestination : Destinations

@Serializable
data object PokemonSelectionDestination : Destinations

@Serializable
data object HomeDestination : Destinations

@Serializable
data object StatsDestination : Destinations

@Serializable
data object TrainingDestination : Destinations

@Serializable
data object StrengthTrainingDestination : Destinations

@Serializable
data object ProfileDestination : Destinations
