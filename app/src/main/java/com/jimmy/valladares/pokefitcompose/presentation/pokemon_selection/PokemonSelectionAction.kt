package com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection

sealed interface PokemonSelectionAction {
    data class SelectPokemon(val pokemon: Pokemon) : PokemonSelectionAction
    data object ConfirmSelection : PokemonSelectionAction
    data object ProceedToHome : PokemonSelectionAction
    data object Back : PokemonSelectionAction
    data object DismissError : PokemonSelectionAction
}
