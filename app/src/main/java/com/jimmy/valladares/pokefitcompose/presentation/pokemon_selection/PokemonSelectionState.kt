package com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection

data class PokemonSelectionState(
    val selectedPokemon: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val canProceed: Boolean = false
)
