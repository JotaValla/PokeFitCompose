package com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection

import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.FitnessGoal
import com.jimmy.valladares.pokefitcompose.presentation.steps_survey.UserStepsProfile
import com.jimmy.valladares.pokefitcompose.presentation.training_survey.UserTrainingProfile

data class PokemonSelectionState(
    val selectedPokemon: Pokemon? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userProfile: CompleteUserProfile? = null,
    val canProceed: Boolean = false
)

enum class Pokemon(
    val drawableRes: Int,
    val displayName: String,
    val description: String
) {
    TOTODILE(
        drawableRes = R.drawable.charmander,
        displayName = "Totodile",
        description = "Pokémon de tipo Agua. Energético y determinado, perfecto para comenzar aventuras fitness."
    ),
    EEVEE(
        drawableRes = R.drawable.eevee,
        displayName = "Eevee",
        description = "Pokémon de tipo Normal. Versátil y adaptable, ideal para diferentes tipos de entrenamiento."
    ),
    PIPLUP(
        drawableRes = R.drawable.gabite,
        displayName = "Piplup",
        description = "Pokémon de tipo Agua. Valiente y orgulloso, excelente compañero para retos fitness."
    )
}

// Perfil completo del usuario que combina información de las encuestas
sealed class CompleteUserProfile(
    val initialGoal: FitnessGoal,
    val selectedPokemon: Pokemon? = null
) {
    data class TrainingProfile(
        val userTrainingProfile: UserTrainingProfile,
        val pokemon: Pokemon? = null
    ) : CompleteUserProfile(
        initialGoal = userTrainingProfile.fitnessGoal,
        selectedPokemon = pokemon
    )
    
    data class StepsProfile(
        val userStepsProfile: UserStepsProfile,
        val pokemon: Pokemon? = null
    ) : CompleteUserProfile(
        initialGoal = userStepsProfile.fitnessGoal,
        selectedPokemon = pokemon
    )
}
