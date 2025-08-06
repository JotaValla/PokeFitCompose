package com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.valladares.pokefitcompose.data.auth.AuthResult
import com.jimmy.valladares.pokefitcompose.data.auth.FirebaseAuthService
import com.jimmy.valladares.pokefitcompose.data.model.PokemonData
import com.jimmy.valladares.pokefitcompose.data.service.FirestoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonSelectionViewModel @Inject constructor(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService
) : ViewModel() {
    
    private val _state = MutableStateFlow(PokemonSelectionState())
    val state = _state.asStateFlow()
    
    private val _events = Channel<PokemonSelectionEvent>()
    val events = _events.receiveAsFlow()
    
    fun onAction(action: PokemonSelectionAction) {
        when (action) {
            is PokemonSelectionAction.SelectPokemon -> {
                selectPokemon(action.pokemon)
            }
            is PokemonSelectionAction.ConfirmSelection -> {
                confirmSelection()
            }
            is PokemonSelectionAction.ProceedToHome -> {
                proceedToHome()
            }
            is PokemonSelectionAction.Back -> {
                navigateBack()
            }
            is PokemonSelectionAction.DismissError -> {
                dismissError()
            }
        }
    }
    
    private fun selectPokemon(pokemonKey: String) {
        val pokemon = PokemonData.availablePokemons.find { it.key == pokemonKey }
        _state.update { currentState ->
            currentState.copy(
                selectedPokemon = pokemonKey,
                canProceed = pokemon != null,
                error = null
            )
        }
        
        // Auto-navegación después de seleccionar Pokémon con feedback visual
        if (pokemon != null) {
            viewModelScope.launch {
                delay(1500) // Pausa para mostrar el mensaje de confirmación
                
                // Mostrar loading mientras se procesa
                _state.update { currentState ->
                    currentState.copy(isLoading = true)
                }
                
                // Guardar en Firebase
                val currentUser = authService.currentUser
                if (currentUser != null) {
                    val updates = mapOf(
                        "selectedPokemon" to pokemonKey
                    )
                    
                    when (val result = firestoreService.updateUserProfile(currentUser.uid, updates)) {
                        is AuthResult.Success -> {
                            _state.update { currentState ->
                                currentState.copy(isLoading = false)
                            }
                            _events.send(PokemonSelectionEvent.NavigateToHome)
                        }
                        is AuthResult.Error -> {
                            _state.update { currentState ->
                                currentState.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                } else {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = "Error: Usuario no autenticado"
                        )
                    }
                }
            }
        }
    }
    
    private fun confirmSelection() {
        val selectedPokemon = _state.value.selectedPokemon
        
        if (selectedPokemon == null) {
            _state.update { currentState ->
                currentState.copy(
                    error = "Por favor selecciona un Pokémon antes de continuar"
                )
            }
            return
        }
        
        // Navegar directamente a Home
        viewModelScope.launch {
            _events.send(PokemonSelectionEvent.NavigateToHome)
        }
    }
    
    private fun proceedToHome() {
        // Navegar a Home sin pasar parámetros adicionales
        viewModelScope.launch {
            _events.send(PokemonSelectionEvent.NavigateToHome)
        }
    }
    
    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(PokemonSelectionEvent.NavigateBack)
        }
    }
    
    private fun dismissError() {
        _state.update { currentState ->
            currentState.copy(error = null)
        }
    }
}
