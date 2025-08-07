package com.jimmy.valladares.pokefitcompose.domain.service

import android.util.Log
import com.jimmy.valladares.pokefitcompose.data.model.WorkoutSession
import com.jimmy.valladares.pokefitcompose.data.model.PokemonLevelUp
import com.jimmy.valladares.pokefitcompose.data.service.FirestoreService
import com.jimmy.valladares.pokefitcompose.data.auth.AuthResult
import com.jimmy.valladares.pokefitcompose.domain.service.ExperienceService
import com.jimmy.valladares.pokefitcompose.domain.service.PokemonProgressService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProgressService @Inject constructor(
    private val firestoreService: FirestoreService,
    private val experienceService: ExperienceService,
    private val pokemonProgressService: PokemonProgressService
) {
    
    companion object {
        private const val TAG = "UserProgressService"
    }
    
    data class LevelUpResult(
        val previousLevel: Int,
        val newLevel: Int,
        val leveledUp: Boolean,
        val expGained: Int,
        val expBreakdown: Map<String, Int>,
        val pokemonLevelUp: PokemonLevelUp? = null
    )
    
    /**
     * Procesa la experiencia ganada después de completar un entrenamiento
     */
    suspend fun processWorkoutCompletion(
        userId: String,
        completedWorkout: WorkoutSession
    ): LevelUpResult? {
        return try {
            Log.d(TAG, "Processing workout completion for user: $userId")
            
            // Obtener el perfil actual del usuario
            val currentProfile = firestoreService.getUserProfile(userId)
            if (currentProfile == null) {
                Log.e(TAG, "User profile not found")
                return null
            }
            
            // Obtener el último entrenamiento anterior (para comparar mejoras)
            val previousWorkouts = firestoreService.getUserWorkouts(userId, limit = 2)
            val previousWorkout = previousWorkouts.find { it.id != completedWorkout.id }
            
            // Obtener racha actual del usuario
            val workoutSummary = firestoreService.getWorkoutSummary(userId)
            val currentStreak = workoutSummary?.currentStreak ?: 0
            
            // Calcular experiencia ganada
            val experienceGain = experienceService.calculateExperienceGain(
                currentWorkout = completedWorkout,
                previousWorkout = previousWorkout,
                currentStreak = currentStreak
            )
            
            Log.d(TAG, "Experience gained: ${experienceGain.totalExp} EXP")
            
            // Calcular nuevo nivel y experiencia total
            val newTotalExp = currentProfile.currentExp + experienceGain.totalExp
            val previousLevel = experienceService.calculateLevel(currentProfile.currentExp)
            val newLevel = experienceService.calculateLevel(newTotalExp)
            
            // Actualizar el perfil del usuario
            val updates = mutableMapOf<String, Any>(
                "currentExp" to newTotalExp,
                "currentLevel" to newLevel,
                "totalWorkouts" to (currentProfile.totalWorkouts + 1),
                "lastActiveAt" to System.currentTimeMillis()
            )
            
            // Si subió de nivel, registrar el evento
            if (newLevel > previousLevel) {
                Log.d(TAG, "User leveled up! Previous: $previousLevel, New: $newLevel")
                // Aquí se podría agregar lógica adicional para evolución de Pokémon, etc.
            }
            
            // Guardar cambios en Firestore
            val updateResult = firestoreService.updateUserProfile(userId, updates)
            
            return when (updateResult) {
                is AuthResult.Success -> {
                    Log.d(TAG, "User profile updated successfully")
                    
                    // Convertir breakdown de List<String> a Map<String, Int>
                    val breakdownMap = mapOf(
                        "completion" to experienceGain.baseExp,
                        "completionRate" to experienceGain.completionBonus,
                        "improvement" to experienceGain.improvementBonus,
                        "duration" to experienceGain.durationBonus,
                        "consistency" to experienceGain.consistencyBonus,
                        "perfectWorkout" to experienceGain.perfectWorkoutBonus
                    )
                    
                    // Procesar experiencia del Pokémon
                    val pokemonLevelUp = pokemonProgressService.processPokemonExpGain(
                        userId = userId,
                        completedWorkout = completedWorkout,
                        userExpGained = experienceGain.totalExp
                    )
                    
                    if (pokemonLevelUp != null) {
                        Log.d(TAG, "Pokemon gained ${pokemonLevelUp.expGained} EXP")
                        if (pokemonLevelUp.newLevel > pokemonLevelUp.previousLevel) {
                            Log.d(TAG, "Pokemon leveled up to ${pokemonLevelUp.newLevel}!")
                        }
                        if (pokemonLevelUp.evolved) {
                            Log.d(TAG, "Pokemon evolved to ${pokemonLevelUp.evolutionName}!")
                        }
                    }
                    
                    LevelUpResult(
                        previousLevel = previousLevel,
                        newLevel = newLevel,
                        leveledUp = newLevel > previousLevel,
                        expGained = experienceGain.totalExp,
                        expBreakdown = breakdownMap,
                        pokemonLevelUp = pokemonLevelUp
                    )
                }
                is AuthResult.Error -> {
                    Log.e(TAG, "Failed to update user profile: ${updateResult.message}")
                    null
                }
            }        } catch (e: Exception) {
            Log.e(TAG, "Error processing workout completion", e)
            null
        }
    }
    
    /**
     * Obtiene información detallada del progreso del usuario
     */
    suspend fun getUserProgressInfo(userId: String): UserProgressInfo? {
        return try {
            val profile = firestoreService.getUserProfile(userId) ?: return null
            
            val currentLevel = experienceService.calculateLevel(profile.currentExp)
            val expForCurrentLevel = experienceService.getExpForCurrentLevel(currentLevel)
            val expForNextLevel = experienceService.getExpForNextLevel(currentLevel)
            val expInCurrentLevel = profile.currentExp - expForCurrentLevel
            val expNeededForNext = expForNextLevel - profile.currentExp
            
            UserProgressInfo(
                currentLevel = currentLevel,
                currentExp = profile.currentExp,
                expInCurrentLevel = expInCurrentLevel,
                expNeededForNext = expNeededForNext,
                expForNextLevel = expForNextLevel,
                totalWorkouts = profile.totalWorkouts,
                progressPercentage = if (expForNextLevel > expForCurrentLevel) {
                    (expInCurrentLevel.toFloat() / (expForNextLevel - expForCurrentLevel).toFloat()) * 100f
                } else 100f
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user progress info", e)
            null
        }
    }
    
    data class UserProgressInfo(
        val currentLevel: Int,
        val currentExp: Int,
        val expInCurrentLevel: Int,
        val expNeededForNext: Int,
        val expForNextLevel: Int,
        val totalWorkouts: Int,
        val progressPercentage: Float
    )
}
