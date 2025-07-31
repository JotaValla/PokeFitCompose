package com.jimmy.valladares.pokefitcompose.presentation.home

sealed interface HomeEvent {
    data object NavigateToTraining : HomeEvent
    data object ShowLevelUpAnimation : HomeEvent
    data object UpdatePokemonStats : HomeEvent
    data class NavigateToTab(val route: String) : HomeEvent
    data class ShowError(val message: String) : HomeEvent
    data class ShowLevelUp(val newLevel: Int) : HomeEvent
}
