package com.jimmy.valladares.pokefitcompose.domain.service

import android.util.Log
import com.jimmy.valladares.pokefitcompose.data.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EvolutionService @Inject constructor(
    private val experienceService: ExperienceService
) {
    
    companion object {
        private const val TAG = "EvolutionService"
    }
    
    /**
     * Verifica si el usuario subió de nivel y si su Pokémon debe evolucionar
     * después de completar un entrenamiento. Retorna la información pero no actualiza Firestore.
     */
    suspend fun checkAndHandleEvolution(
        userId: String,
        previousTotalExp: Int,
        newTotalExp: Int,
        currentUserProfile: UserProfile
    ): EvolutionCheckResult {
        try {
            Log.d(TAG, "Checking evolution for user: $userId")
            Log.d(TAG, "Previous exp: $previousTotalExp, New exp: $newTotalExp")
            
            val evolutionResult = experienceService.checkLevelUpAndEvolution(
                previousTotalExp = previousTotalExp,
                newTotalExp = newTotalExp,
                currentPokemon = currentUserProfile.selectedPokemon
            )
            
            if (evolutionResult.leveledUp) {
                Log.d(TAG, "User leveled up! Level ${evolutionResult.previousLevel} -> ${evolutionResult.newLevel}")
                
                if (evolutionResult.shouldEvolve && evolutionResult.evolution != null) {
                    Log.d(TAG, "Pokémon should evolve: ${currentUserProfile.selectedPokemon} -> ${evolutionResult.evolution}")
                    
                    return EvolutionCheckResult(
                        leveledUp = true,
                        evolved = true,
                        previousLevel = evolutionResult.previousLevel,
                        newLevel = evolutionResult.newLevel,
                        previousPokemon = currentUserProfile.selectedPokemon,
                        newPokemon = evolutionResult.evolution,
                        evolutionSaved = false, // Se manejará externamente
                        errorMessage = null
                    )
                } else {
                    // Solo subió de nivel, no evolucionó
                    return EvolutionCheckResult(
                        leveledUp = true,
                        evolved = false,
                        previousLevel = evolutionResult.previousLevel,
                        newLevel = evolutionResult.newLevel,
                        previousPokemon = currentUserProfile.selectedPokemon,
                        newPokemon = currentUserProfile.selectedPokemon,
                        evolutionSaved = true,
                        errorMessage = null
                    )
                }
            }
            
            // No hubo cambio de nivel
            return EvolutionCheckResult(
                leveledUp = false,
                evolved = false,
                previousLevel = evolutionResult.previousLevel,
                newLevel = evolutionResult.newLevel,
                previousPokemon = currentUserProfile.selectedPokemon,
                newPokemon = currentUserProfile.selectedPokemon,
                evolutionSaved = true,
                errorMessage = null
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking evolution", e)
            return EvolutionCheckResult(
                leveledUp = false,
                evolved = false,
                previousLevel = 1,
                newLevel = 1,
                previousPokemon = currentUserProfile.selectedPokemon,
                newPokemon = currentUserProfile.selectedPokemon,
                evolutionSaved = false,
                errorMessage = e.message
            )
        }
    }
    
    /**
     * Obtiene el nombre completo del Pokémon para mostrar en mensajes
     */
    fun getPokemonDisplayName(pokemonKey: String): String {
        return when (pokemonKey.lowercase()) {
            "torchic" -> "Torchic"
            "combusken" -> "Combusken"
            "machop" -> "Machop"
            "machoke" -> "Machoke"
            "gible" -> "Gible"
            "gabite" -> "Gabite"
            else -> pokemonKey.replaceFirstChar { it.uppercase() }
        }
    }
}

/**
 * Resultado de verificar evolución después de un entrenamiento
 */
data class EvolutionCheckResult(
    val leveledUp: Boolean,
    val evolved: Boolean,
    val previousLevel: Int,
    val newLevel: Int,
    val previousPokemon: String,
    val newPokemon: String,
    val evolutionSaved: Boolean,
    val errorMessage: String?
)
