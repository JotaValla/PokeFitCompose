package com.jimmy.valladares.pokefitcompose.domain.service

import android.util.Log
import com.jimmy.valladares.pokefitcompose.data.model.UserPokemon
import com.jimmy.valladares.pokefitcompose.data.model.PokemonLevelUp
import com.jimmy.valladares.pokefitcompose.data.model.WorkoutSession
import com.jimmy.valladares.pokefitcompose.data.service.FirestoreService
import com.jimmy.valladares.pokefitcompose.data.auth.AuthResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class PokemonProgressService @Inject constructor(
    private val firestoreService: FirestoreService
) {
    
    companion object {
        private const val TAG = "PokemonProgressService"
        private const val BASE_EXP_REQUIREMENT = 100
        private const val EXP_MULTIPLIER = 1.2 // Cada nivel requiere 20% más EXP
    }
    
    /**
     * Procesa la experiencia ganada por el Pokémon después de un entrenamiento
     */
    suspend fun processPokemonExpGain(
        userId: String,
        completedWorkout: WorkoutSession,
        userExpGained: Int
    ): PokemonLevelUp? {
        return try {
            Log.d(TAG, "Processing Pokemon EXP gain for user: $userId")
            
            // Obtener el Pokémon seleccionado del usuario
            val selectedPokemon = getSelectedPokemon(userId)
            if (selectedPokemon == null) {
                Log.e(TAG, "No selected Pokemon found for user")
                return null
            }
            
            // Calcular la EXP que gana el Pokémon (un porcentaje de la EXP del usuario)
            val pokemonExpGain = calculatePokemonExpGain(userExpGained, completedWorkout)
            Log.d(TAG, "Pokemon will gain $pokemonExpGain EXP")
            
            // Aplicar la experiencia al Pokémon
            val levelUpResult = applyExpToPokemon(selectedPokemon, pokemonExpGain)
            
            // Guardar los cambios en Firestore
            val updateResult = updatePokemonInFirestore(userId, levelUpResult.updatedPokemon)
            
            return if (updateResult is AuthResult.Success) {
                Log.d(TAG, "Pokemon updated successfully")
                PokemonLevelUp(
                    previousLevel = levelUpResult.previousLevel,
                    newLevel = levelUpResult.newLevel,
                    expGained = pokemonExpGain,
                    totalExp = levelUpResult.updatedPokemon.totalExp,
                    evolved = levelUpResult.evolved,
                    evolutionName = if (levelUpResult.evolved) levelUpResult.updatedPokemon.getEvolutionName() else ""
                )
            } else {
                Log.e(TAG, "Failed to update Pokemon in Firestore")
                null
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing Pokemon EXP gain", e)
            null
        }
    }
    
    /**
     * Obtiene el Pokémon seleccionado del usuario
     */
    private suspend fun getSelectedPokemon(userId: String): UserPokemon? {
        return try {
            val userProfile = firestoreService.getUserProfile(userId)
            if (userProfile?.selectedPokemon != null) {
                // Si ya existe un Pokémon seleccionado, retornarlo
                getUserPokemon(userId, userProfile.selectedPokemon)
            } else {
                // Si no existe, crear uno nuevo basado en el Pokémon seleccionado en el perfil
                createDefaultPokemon(userId, userProfile?.selectedPokemon ?: "machop")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting selected Pokemon", e)
            null
        }
    }
    
    /**
     * Obtiene un Pokémon específico del usuario
     */
    private suspend fun getUserPokemon(userId: String, pokemonName: String): UserPokemon? {
        // Aquí implementarías la lógica para obtener el Pokémon de Firestore
        // Por ahora, crear uno por defecto
        return createDefaultPokemon(userId, pokemonName)
    }
    
    /**
     * Crea un Pokémon por defecto para el usuario
     */
    private suspend fun createDefaultPokemon(userId: String, pokemonName: String): UserPokemon {
        val displayName = pokemonName.replaceFirstChar { it.uppercase() }
        
        val pokemon = UserPokemon(
            id = "${userId}_${pokemonName}",
            pokemonName = pokemonName.lowercase(),
            displayName = displayName,
            level = 1,
            currentExp = 0,
            maxExp = BASE_EXP_REQUIREMENT,
            totalExp = 0,
            isSelected = true,
            obtainedAt = System.currentTimeMillis(),
            lastExpGain = 0L
        )
        
        // Guardar el nuevo Pokémon en Firestore
        updatePokemonInFirestore(userId, pokemon)
        
        return pokemon
    }
    
    /**
     * Calcula cuánta EXP gana el Pokémon basado en la EXP del usuario
     */
    private fun calculatePokemonExpGain(userExpGained: Int, workout: WorkoutSession): Int {
        // El Pokémon gana un porcentaje de la EXP del usuario
        var pokemonExp = (userExpGained * 0.6).toInt() // 60% de la EXP del usuario
        
        // Bonificación por número de ejercicios completados
        val exerciseBonus = workout.exercises.size * 2
        
        // Bonificación por duración del entrenamiento
        val durationMinutes = workout.totalDurationSeconds / 60
        val durationBonus = when {
            durationMinutes >= 30 -> 10
            durationMinutes >= 15 -> 5
            else -> 0
        }
        
        pokemonExp += exerciseBonus + durationBonus
        
        return pokemonExp.coerceAtLeast(5) // Mínimo 5 EXP
    }
    
    /**
     * Aplica experiencia al Pokémon y maneja subidas de nivel
     */
    private fun applyExpToPokemon(pokemon: UserPokemon, expGain: Int): PokemonUpdateResult {
        var currentPokemon = pokemon.copy(
            totalExp = pokemon.totalExp + expGain,
            lastExpGain = System.currentTimeMillis()
        )
        
        var newCurrentExp = pokemon.currentExp + expGain
        var evolved = false
        val previousLevel = pokemon.level
        var newLevel = pokemon.level
        
        // Procesar subidas de nivel
        while (newCurrentExp >= currentPokemon.maxExp) {
            newCurrentExp -= currentPokemon.maxExp
            newLevel++
            
            // Calcular nueva EXP máxima para el siguiente nivel
            val newMaxExp = calculateExpForLevel(newLevel + 1)
            
            currentPokemon = currentPokemon.copy(
                level = newLevel,
                maxExp = newMaxExp
            )
            
            Log.d(TAG, "Pokemon leveled up to level $newLevel!")
            
            // Verificar evolución
            if (currentPokemon.canEvolve() && !evolved) {
                evolved = true
                val evolutionName = currentPokemon.getEvolutionName()
                currentPokemon = currentPokemon.copy(
                    pokemonName = evolutionName.lowercase(),
                    displayName = evolutionName.replaceFirstChar { it.uppercase() }
                )
                Log.d(TAG, "Pokemon evolved to ${currentPokemon.displayName}!")
            }
        }
        
        currentPokemon = currentPokemon.copy(currentExp = newCurrentExp)
        
        return PokemonUpdateResult(
            updatedPokemon = currentPokemon,
            previousLevel = previousLevel,
            newLevel = newLevel,
            evolved = evolved
        )
    }
    
    /**
     * Calcula la EXP requerida para un nivel específico
     */
    private fun calculateExpForLevel(level: Int): Int {
        return (BASE_EXP_REQUIREMENT * EXP_MULTIPLIER.pow(level - 1)).toInt()
    }
    
    /**
     * Actualiza el Pokémon en Firestore
     */
    private suspend fun updatePokemonInFirestore(userId: String, pokemon: UserPokemon): AuthResult {
        val pokemonData = mapOf(
            "id" to pokemon.id,
            "pokemonName" to pokemon.pokemonName,
            "displayName" to pokemon.displayName,
            "level" to pokemon.level,
            "currentExp" to pokemon.currentExp,
            "maxExp" to pokemon.maxExp,
            "totalExp" to pokemon.totalExp,
            "isSelected" to pokemon.isSelected,
            "obtainedAt" to pokemon.obtainedAt,
            "lastExpGain" to pokemon.lastExpGain
        )
        
        return firestoreService.updateUserProfile(userId, mapOf("selectedPokemonData" to pokemonData))
    }
    
    /**
     * Resultado interno para actualizaciones de Pokémon
     */
    private data class PokemonUpdateResult(
        val updatedPokemon: UserPokemon,
        val previousLevel: Int,
        val newLevel: Int,
        val evolved: Boolean
    )
}
