package com.jimmy.valladares.pokefitcompose.data.repository

import com.jimmy.valladares.pokefitcompose.data.local.UserPreferences
import com.jimmy.valladares.pokefitcompose.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface UserRepository {
    suspend fun getCurrentUser(): UserProfile?
    fun getCurrentUserFlow(): Flow<UserProfile?>
    suspend fun updateUser(userProfile: UserProfile)
}

// Mock implementation - reemplazar con implementación real más tarde
class MockUserRepository @Inject constructor(
    private val userPreferences: UserPreferences
) : UserRepository {
    
    // Simulamos un usuario base
    private fun createBaseUser(selectedPokemon: String = "eevee"): UserProfile {
        return UserProfile(
            id = "user123",
            name = "Usuario Demo",
            email = "demo@pokefit.com",
            fitnessGoal = "training",
            selectedPokemon = selectedPokemon,
            currentLevel = 5,
            currentExp = 75,
            totalWorkouts = 12,
            streakDays = 2
        )
    }
    
    override suspend fun getCurrentUser(): UserProfile {
        val currentPokemon = userPreferences.getCurrentPokemon()
        val existingProfile = userPreferences.userProfile.value
        
        return existingProfile ?: createBaseUser(currentPokemon).also {
            userPreferences.setUserProfile(it)
        }
    }
    
    override fun getCurrentUserFlow(): Flow<UserProfile?> {
        return userPreferences.userProfile.map { profile ->
            profile ?: createBaseUser(userPreferences.getCurrentPokemon())
        }
    }
    
    override suspend fun updateUser(userProfile: UserProfile) {
        userPreferences.setUserProfile(userProfile)
    }
}
