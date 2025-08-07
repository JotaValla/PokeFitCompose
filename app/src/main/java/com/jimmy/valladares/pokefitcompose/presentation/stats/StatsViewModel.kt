package com.jimmy.valladares.pokefitcompose.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.valladares.pokefitcompose.data.auth.FirebaseAuthService
import com.jimmy.valladares.pokefitcompose.data.service.FirestoreService
import com.jimmy.valladares.pokefitcompose.data.model.PokemonData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val authService: FirebaseAuthService
) : ViewModel() {
    
    private val _state = MutableStateFlow(StatsState())
    val state: StateFlow<StatsState> = _state.asStateFlow()
    
    init {
        loadUserStats()
    }
    
    private fun loadUserStats() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            try {
                val userId = authService.currentUserId
                if (userId != null) {
                    // Cargar datos del usuario desde Firestore
                    val userProfile = firestoreService.getUserProfile(userId)
                    userProfile?.let { profile ->
                        val pokemonInfo = PokemonData.availablePokemons.find { 
                            it.key == profile.selectedPokemon 
                        }
                        
                        // Calcular experiencia total: (nivel - 1) * 1000 + experiencia actual
                        val totalExperience = (profile.currentLevel - 1) * 1000 + profile.currentExp
                        
                        _state.value = _state.value.copy(
                            pokemonName = pokemonInfo?.name ?: "Eevee",
                            selectedPokemon = profile.selectedPokemon,
                            currentLevel = profile.currentLevel,
                            currentExp = profile.currentExp,
                            maxExp = calculateExpForNextLevel(profile.currentLevel),
                            totalExp = totalExperience,
                            totalLevelsGained = profile.currentLevel - 1
                        )
                    }
                    
                    // Obtener estadísticas reales de workouts
                    val workoutStats = firestoreService.getWorkoutStats(userId)
                    _state.value = _state.value.copy(
                        averageDaysPerWeek = workoutStats.averageDaysPerWeek,
                        maxStreak = workoutStats.maxStreak,
                        averageMinutes = workoutStats.averageMinutes,
                        totalWorkouts = workoutStats.totalWorkouts,
                        weeklyExp = workoutStats.weeklyExp,
                        previousWeekExp = workoutStats.previousWeekExp,
                        isLoading = false
                    )
                } else {
                    // Usuario no autenticado, usar valores por defecto
                    _state.value = _state.value.copy(
                        pokemonName = "Eevee",
                        selectedPokemon = "eevee",
                        currentLevel = 1,
                        currentExp = 0,
                        maxExp = 1000,
                        totalExp = 0,
                        totalLevelsGained = 0,
                        averageDaysPerWeek = 0f,
                        maxStreak = 0,
                        averageMinutes = 0,
                        totalWorkouts = 0,
                        weeklyExp = 0,
                        previousWeekExp = 0,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // En caso de error, usar valores por defecto
                _state.value = _state.value.copy(
                    pokemonName = "Eevee",
                    selectedPokemon = "eevee",
                    currentLevel = 1,
                    currentExp = 0,
                    maxExp = 1000,
                    totalExp = 0,
                    totalLevelsGained = 0,
                    averageDaysPerWeek = 0f,
                    maxStreak = 0,
                    averageMinutes = 0,
                    totalWorkouts = 0,
                    weeklyExp = 0,
                    previousWeekExp = 0,
                    isLoading = false
                )
            }
        }
    }
    
    private fun calculateExpForNextLevel(currentLevel: Int): Int {
        return 1000 // Por simplicidad, cada nivel requiere 1000 EXP
    }
}
