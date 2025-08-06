package com.jimmy.valladares.pokefitcompose.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.valladares.pokefitcompose.data.auth.FirebaseAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authService: FirebaseAuthService
) : ViewModel() {
    
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()
    
    init {
        checkAuthenticationState()
    }
    
    private fun checkAuthenticationState() {
        viewModelScope.launch {
            // Observar cambios en el estado de autenticación
            authService.authStateFlow.collect { user ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    isAuthenticated = user != null
                )
            }
        }
    }
}

data class MainState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false
)
