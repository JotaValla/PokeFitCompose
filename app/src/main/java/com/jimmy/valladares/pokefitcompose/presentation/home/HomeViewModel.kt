package com.jimmy.valladares.pokefitcompose.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.valladares.pokefitcompose.data.auth.FirebaseAuthService
import com.jimmy.valladares.pokefitcompose.data.service.FirestoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.random.Random

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    init {
        handleAction(HomeAction.LoadUserProfile)
    }

    fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.LoadUserProfile -> loadUserProfile()
            is HomeAction.RefreshData -> refreshData()
            is HomeAction.StartTraining -> startTraining()
            is HomeAction.NavigateToTab -> navigateToTab(action.tab)
            is HomeAction.RecordTraining -> recordTraining(action.expGained)
            is HomeAction.UpdateLevel -> updateLevel()
            is HomeAction.ClearError -> clearError()
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val currentUser = authService.currentUser
                if (currentUser != null) {
                    val userProfile = firestoreService.getUserProfile(currentUser.uid)
                    
                    // Simular datos de progreso semanal
                    val mockWeeklyProgress = listOf(
                        DayProgress("Lun", true, 10),
                        DayProgress("Mar", true, 12),
                        DayProgress("Mie", false, 0),
                        DayProgress("Jue", false, 0),
                        DayProgress("Vie", false, 0),
                        DayProgress("Sab", false, 0),
                        DayProgress("Dom", false, 0)
                    )

                    // Obtener Pokemon seleccionado del perfil del usuario
                    val selectedPokemon = userProfile?.selectedPokemon ?: "eevee"
                    val pokemonName = getPokemonName(selectedPokemon)
                    val currentLevel = userProfile?.currentLevel ?: 1
                    val currentExp = userProfile?.currentExp ?: 0
                    val maxExp = calculateMaxExp(currentLevel)

                    _state.update {
                        it.copy(
                            userProfile = userProfile,
                            selectedPokemon = selectedPokemon,
                            pokemonName = pokemonName,
                            currentLevel = currentLevel,
                            currentExp = currentExp,
                            maxExp = maxExp,
                            streakDays = userProfile?.streakDays ?: 0,
                            weeklyProgress = mockWeeklyProgress,
                            todayTrainings = 0,
                            expToNextLevel = maxExp - currentExp,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Usuario no autenticado"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar los datos: ${e.message}"
                    )
                }
            }
        }
    }

    private fun refreshData() {
        loadUserProfile()
    }

    private fun startTraining() {
        viewModelScope.launch {
            _events.send(HomeEvent.NavigateToTraining)
        }
    }

    private fun navigateToTab(tab: BottomNavItem) {
        viewModelScope.launch {
            _events.send(HomeEvent.NavigateToTab(tab.route))
        }
    }

    private fun recordTraining(expGained: Int) {
        viewModelScope.launch {
            val currentState = _state.value
            val newExp = currentState.currentExp + expGained
            val maxExp = currentState.maxExp
            
            if (newExp >= maxExp) {
                // Level up!
                val newLevel = currentState.currentLevel + 1
                val remainingExp = newExp - maxExp
                val newMaxExp = calculateMaxExp(newLevel)
                
                _state.update {
                    it.copy(
                        currentLevel = newLevel,
                        currentExp = remainingExp,
                        maxExp = newMaxExp,
                        expToNextLevel = newMaxExp - remainingExp,
                        todayTrainings = it.todayTrainings + 1
                    )
                }
                
                _events.send(HomeEvent.ShowLevelUp(newLevel))
            } else {
                _state.update {
                    it.copy(
                        currentExp = newExp,
                        expToNextLevel = maxExp - newExp,
                        todayTrainings = it.todayTrainings + 1
                    )
                }
            }
            
            _events.send(HomeEvent.UpdatePokemonStats)
        }
    }

    private fun updateLevel() {
        // Lógica para actualizar nivel manualmente si es necesario
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun calculateMaxExp(level: Int): Int {
        // Fórmula progresiva: cada nivel requiere más exp
        return (100 * (level * 1.5)).toInt()
    }

    private fun getPokemonName(pokemonKey: String): String {
        return when (pokemonKey) {
            "torchic" -> "Torchic"
            "machop" -> "Machop"
            "gible" -> "Gible"
            "charmander" -> "Charmander"
            "eevee" -> "Eevee"
            "gabite" -> "Gabite"
            "totodile" -> "Totodile"
            "piplup" -> "Piplup"
            else -> "Eevee"
        }
    }
}
