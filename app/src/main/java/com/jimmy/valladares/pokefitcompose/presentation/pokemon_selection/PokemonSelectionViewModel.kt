package com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class PokemonSelectionViewModel @Inject constructor() : ViewModel() {
    
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
    
    // Función para inicializar el perfil del usuario desde la navegación
    fun initializeUserProfile(profile: CompleteUserProfile) {
        _state.update { currentState ->
            currentState.copy(userProfile = profile)
        }
    }
    
    private fun selectPokemon(pokemon: Pokemon) {
        _state.update { currentState ->
            currentState.copy(
                selectedPokemon = pokemon,
                canProceed = true,
                error = null
            )
        }
        
        // Auto-navegación después de seleccionar Pokémon con feedback visual
        viewModelScope.launch {
            delay(1500) // Pausa para mostrar el mensaje de confirmación
            
            // Mostrar loading mientras se procesa
            _state.update { currentState ->
                currentState.copy(isLoading = true)
            }
            
            delay(1000) // Simular procesamiento para que se vea el loading
            
            // Navegar directamente a Home
            _events.send(PokemonSelectionEvent.NavigateToHome)
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
