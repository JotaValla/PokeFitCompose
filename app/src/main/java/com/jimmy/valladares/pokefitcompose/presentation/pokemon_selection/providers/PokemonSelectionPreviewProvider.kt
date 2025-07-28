package com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection.providers

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.FitnessGoal
import com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection.CompleteUserProfile
import com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection.Pokemon
import com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection.PokemonSelectionState
import com.jimmy.valladares.pokefitcompose.presentation.training_survey.TrainingGoal
import com.jimmy.valladares.pokefitcompose.presentation.training_survey.UserTrainingProfile
import com.jimmy.valladares.pokefitcompose.presentation.steps_survey.UserStepsProfile

class PokemonSelectionPreviewProvider : PreviewParameterProvider<PokemonSelectionState> {
    override val values = sequenceOf(
        // Estado inicial - ningún pokemon seleccionado (perfil de entrenamiento)
        PokemonSelectionState(
            userProfile = CompleteUserProfile.TrainingProfile(
                userTrainingProfile = UserTrainingProfile(
                    fitnessGoal = FitnessGoal.IMPROVE_TRAINING,
                    trainingGoal = TrainingGoal.VELOCITY
                )
            )
        ),
        
        // Estado inicial - ningún pokemon seleccionado (perfil de pasos)
        PokemonSelectionState(
            userProfile = CompleteUserProfile.StepsProfile(
                userStepsProfile = UserStepsProfile(
                    fitnessGoal = FitnessGoal.EXIT_SEDENTARY,
                    dailyStepsGoal = 8000
                )
            )
        ),
        
        // Totodile seleccionado
        PokemonSelectionState(
            selectedPokemon = Pokemon.TOTODILE,
            canProceed = true,
            userProfile = CompleteUserProfile.TrainingProfile(
                userTrainingProfile = UserTrainingProfile(
                    fitnessGoal = FitnessGoal.IMPROVE_TRAINING,
                    trainingGoal = TrainingGoal.VELOCITY
                ),
                pokemon = Pokemon.TOTODILE
            )
        ),
        
        // Eevee seleccionado
        PokemonSelectionState(
            selectedPokemon = Pokemon.EEVEE,
            canProceed = true,
            userProfile = CompleteUserProfile.StepsProfile(
                userStepsProfile = UserStepsProfile(
                    fitnessGoal = FitnessGoal.EXIT_SEDENTARY,
                    dailyStepsGoal = 10000
                ),
                pokemon = Pokemon.EEVEE
            )
        ),
        
        // Piplup seleccionado
        PokemonSelectionState(
            selectedPokemon = Pokemon.PIPLUP,
            canProceed = true,
            userProfile = CompleteUserProfile.TrainingProfile(
                userTrainingProfile = UserTrainingProfile(
                    fitnessGoal = FitnessGoal.IMPROVE_TRAINING,
                    trainingGoal = TrainingGoal.STRENGTH
                ),
                pokemon = Pokemon.PIPLUP
            )
        ),
        
        // Estado de loading durante navegación
        PokemonSelectionState(
            selectedPokemon = Pokemon.EEVEE,
            isLoading = true,
            canProceed = true,
            userProfile = CompleteUserProfile.StepsProfile(
                userStepsProfile = UserStepsProfile(
                    fitnessGoal = FitnessGoal.EXIT_SEDENTARY,
                    dailyStepsGoal = 12000
                ),
                pokemon = Pokemon.EEVEE
            )
        ),
        
        // Estado de error
        PokemonSelectionState(
            error = "Error al guardar tu selección. Inténtalo de nuevo.",
            userProfile = CompleteUserProfile.TrainingProfile(
                userTrainingProfile = UserTrainingProfile(
                    fitnessGoal = FitnessGoal.IMPROVE_TRAINING,
                    trainingGoal = TrainingGoal.RESISTANCE
                )
            )
        )
    )
}
