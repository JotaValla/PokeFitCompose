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
                        
                        _state.value = _state.value.copy(
                            pokemonName = pokemonInfo?.name ?: "Eevee",
                            selectedPokemon = profile.selectedPokemon,
                            currentLevel = calculateLevel(profile.totalExperience),
                            currentExp = calculateCurrentLevelExp(profile.totalExperience),
                            maxExp = calculateExpForNextLevel(calculateLevel(profile.totalExperience)),
                            totalExp = profile.totalExperience,
                            totalLevelsGained = calculateLevel(profile.totalExperience) - 1
                        )
                    }
                    
                    // Por ahora usar datos quemados para las estadísticas de actividad
                    // En el futuro, estos datos vendrán de una colección de workouts en Firestore
                    _state.value = _state.value.copy(
                        averageDaysPerWeek = 4.2f,
                        maxStreak = 12,
                        averageMinutes = 35,
                        totalWorkouts = 48,
                        weeklyExp = 1250,
                        previousWeekExp = 980,
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
    
    private fun calculateLevel(totalExp: Int): Int {
        return (totalExp / 1000) + 1
    }
    
    private fun calculateCurrentLevelExp(totalExp: Int): Int {
        return totalExp % 1000
    }
    
    private fun calculateExpForNextLevel(level: Int): Int {
        return 1000 // Por simplicidad, cada nivel requiere 1000 EXP
    }
}
