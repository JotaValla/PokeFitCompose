package com.jimmy.valladares.pokefitcompose.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.valladares.pokefitcompose.data.auth.FirebaseAuthService
import com.jimmy.valladares.pokefitcompose.data.service.FirestoreService
import com.jimmy.valladares.pokefitcompose.domain.service.UserProgressService
import com.jimmy.valladares.pokefitcompose.domain.service.ExperienceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.random.Random
import java.util.*
import java.text.SimpleDateFormat

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService,
    private val userProgressService: UserProgressService,
    private val experienceService: ExperienceService
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
    
    /**
     * Método público para refrescar datos (se puede llamar desde la UI)
     */
    fun refreshData() {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val currentUser = authService.currentUser
                if (currentUser != null) {
                    val userProfile = firestoreService.getUserProfile(currentUser.uid)
                    //verificacion si el usuario existe.
                    if (userProfile != null) {
                        //recupero el entrenamiento.
                        val todayTrainings = getTodayTrainingsCount(currentUser.uid)
                        
                        // Obtener racha actual y progreso semanal
                        val workoutSummary = firestoreService.getWorkoutSummary(currentUser.uid)
                        val currentStreak = workoutSummary?.currentStreak ?: 0
                        val weeklyProgress = getWeeklyProgress(currentUser.uid)

                        // Obtener Pokemon seleccionado del perfil del usuario
                        val selectedPokemon = userProfile.selectedPokemon?.takeIf { it.isNotBlank() } ?: "eevee"
                        val pokemonName = getPokemonName(selectedPokemon)
                        
                        // Usar directamente los valores del perfil de Firebase (igual que StatsScreen)
                        val currentLevel = userProfile.currentLevel
                        val currentExp = userProfile.currentExp
                        val maxExp = experienceService.getExpForNextLevel(currentLevel)
                        val expToNextLevel = maxExp - currentExp

                        println("HomeViewModel: Loading Firebase data - Level: $currentLevel, EXP: $currentExp, Next: $expToNextLevel")

                        _state.update {
                            it.copy(
                                userProfile = userProfile,
                                selectedPokemon = selectedPokemon,
                                pokemonName = pokemonName,
                                currentLevel = currentLevel,
                                currentExp = currentExp,
                                maxExp = maxExp,
                                streakDays = currentStreak,
                                weeklyProgress = weeklyProgress,
                                todayTrainings = todayTrainings,
                                expToNextLevel = expToNextLevel,
                                isLoading = false
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Perfil de usuario no encontrado"
                            )
                        }
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
            //verificar si se ha llegado al maximo de experincia para el nivel, posterior a esto se
            //se sube el nivel
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
    
    /**
     * Obtiene el número de entrenamientos realizados hoy (usando filtrado del lado cliente)
     */
    private suspend fun getTodayTrainingsCount(userId: String): Int {
        return try {
            val today = System.currentTimeMillis()
            val startOfDay = getStartOfDay(today)
            val endOfDay = getEndOfDay(today)
            
            // Usar getUserWorkouts y filtrar en el cliente para evitar problemas de índice
            val allWorkouts = firestoreService.getUserWorkouts(userId, limit = 50)
            
            val todayWorkouts = allWorkouts.filter { workout ->
                workout.completedAt >= startOfDay && workout.completedAt <= endOfDay
            }
            
            Log.d("HomeViewModel", "Found ${todayWorkouts.size} workouts today out of ${allWorkouts.size} total workouts")
            todayWorkouts.size
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error getting today's trainings: ${e.message}")
            0
        }
    }
    
    /**
     * Obtiene el progreso semanal basado en entrenamientos reales (usando filtrado del lado cliente)
     */
    private suspend fun getWeeklyProgress(userId: String): List<DayProgress> {
        return try {
            val today = Calendar.getInstance()
            val dayNames = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
            val weekProgress = mutableListOf<DayProgress>()
            
            // Obtener todos los workouts recientes (últimos 7 días aproximadamente)
            val allWorkouts = firestoreService.getUserWorkouts(userId, limit = 50)
            
            // Obtener el lunes de esta semana
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            
            // Crear progreso para cada día de la semana (Lun-Dom)
            for (i in 0..6) {
                val dayStart = calendar.timeInMillis
                val dayEnd = getEndOfDay(dayStart)
                
                // Filtrar entrenamientos de este día en el cliente
                val dayWorkouts = allWorkouts.filter { workout ->
                    workout.completedAt >= dayStart && workout.completedAt <= dayEnd
                }
                
                val dayIndex = if (i == 6) 0 else i + 1 // Ajustar para que Dom sea 0
                val isCompleted = dayWorkouts.isNotEmpty()
                val expGained = if (isCompleted) dayWorkouts.size * 10 else 0 // Estimación simple
                
                weekProgress.add(
                    DayProgress(
                        dayName = dayNames[dayIndex],
                        isCompleted = isCompleted,
                        expGained = expGained
                    )
                )
                
                // Avanzar al siguiente día
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            
            Log.d("HomeViewModel", "Weekly progress calculated with ${allWorkouts.size} workouts")
            weekProgress
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error getting weekly progress: ${e.message}")
            // Fallback a datos vacíos
            listOf(
                DayProgress("Lun", false, 0),
                DayProgress("Mar", false, 0),
                DayProgress("Mié", false, 0),
                DayProgress("Jue", false, 0),
                DayProgress("Vie", false, 0),
                DayProgress("Sáb", false, 0),
                DayProgress("Dom", false, 0)
            )
        }
    }
    
    /**
     * Obtiene el inicio del día en milisegundos
     */
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    /**
     * Obtiene el final del día en milisegundos
     */
    private fun getEndOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
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
