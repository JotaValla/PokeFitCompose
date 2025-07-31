package com.jimmy.valladares.pokefitcompose.data.local

import com.jimmy.valladares.pokefitcompose.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor() {
    
    private val _selectedPokemon = MutableStateFlow("eevee")
    val selectedPokemon: StateFlow<String> = _selectedPokemon.asStateFlow()
    
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    fun setSelectedPokemon(pokemon: String) {
        _selectedPokemon.value = pokemon
        // Actualizar también el perfil del usuario
        _userProfile.value = _userProfile.value?.copy(selectedPokemon = pokemon)
    }
    
    fun setUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        _selectedPokemon.value = profile.selectedPokemon
    }
    
    fun getCurrentPokemon(): String = _selectedPokemon.value
}
