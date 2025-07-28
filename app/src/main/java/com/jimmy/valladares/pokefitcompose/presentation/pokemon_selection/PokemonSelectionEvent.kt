package com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection

sealed interface PokemonSelectionEvent {
    data object NavigateToHome : PokemonSelectionEvent
    data object NavigateBack : PokemonSelectionEvent
    data class ShowError(val message: String) : PokemonSelectionEvent
    data class ShowConfirmation(val pokemon: Pokemon) : PokemonSelectionEvent
}
