package com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection

import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.FitnessGoal
import com.jimmy.valladares.pokefitcompose.presentation.steps_survey.UserStepsProfile
import com.jimmy.valladares.pokefitcompose.presentation.training_survey.UserTrainingProfile

enum class Pokemon(
    val gifFileName: String,
    val displayName: String,
    val description: String,
    val key: String
) {
    TORCHIC(
        gifFileName = "torchic.gif",
        displayName = "Torchic",
        description = "Pokémon de tipo Fuego. Energético y determinado, perfecto para comenzar aventuras fitness.",
        key = "torchic"
    ),
    MACHOP(
        gifFileName = "machop.gif",
        displayName = "Machop", 
        description = "Pokémon de tipo Lucha. Fuerte y resistente, ideal para entrenamientos intensos.",
        key = "machop"
    ),
    GIBLE(
        gifFileName = "gible.gif",
        displayName = "Gible",
        description = "Pokémon de tipo Dragón/Tierra. Feroz y ambicioso, excelente para alcanzar metas.",
        key = "gible"
    );

    // Helper para obtener la URI del asset
    fun getAssetUri(): String = "file:///android_asset/$gifFileName"
    
    // Helper para obtener el drawable resource ID (para fallback)
    fun getDrawableResId(): Int = when (this) {
        TORCHIC -> R.drawable.torchic
        MACHOP -> R.drawable.machop  
        GIBLE -> R.drawable.gible
    }
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
