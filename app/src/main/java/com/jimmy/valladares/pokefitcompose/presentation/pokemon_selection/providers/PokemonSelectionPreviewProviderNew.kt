package com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection.providers

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection.PokemonSelectionState

class PokemonSelectionPreviewProvider : PreviewParameterProvider<PokemonSelectionState> {
    override val values = sequenceOf(
        // Estado inicial - ningún pokemon seleccionado
        PokemonSelectionState(),
        
        // Estado con Torchic seleccionado
        PokemonSelectionState(
            selectedPokemon = "torchic",
            canProceed = true
        ),
        
        // Estado con Machop seleccionado
        PokemonSelectionState(
            selectedPokemon = "machop",
            canProceed = true
        ),
        
        // Estado con Gible seleccionado
        PokemonSelectionState(
            selectedPokemon = "gible", 
            canProceed = true
        ),
        
        // Estado cargando
        PokemonSelectionState(
            selectedPokemon = "torchic",
            isLoading = true
        ),
        
        // Estado con error
        PokemonSelectionState(
            error = "Error al seleccionar pokémon"
        )
    )
}
