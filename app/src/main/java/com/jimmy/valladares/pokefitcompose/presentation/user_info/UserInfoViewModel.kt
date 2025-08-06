package com.jimmy.valladares.pokefitcompose.presentation.user_info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.valladares.pokefitcompose.data.auth.AuthResult
import com.jimmy.valladares.pokefitcompose.data.auth.FirebaseAuthService
import com.jimmy.valladares.pokefitcompose.data.model.UserProfile
import com.jimmy.valladares.pokefitcompose.data.service.FirestoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService
) : ViewModel() {
    
    private val _state = MutableStateFlow(UserInfoState())
    val state: StateFlow<UserInfoState> = _state.asStateFlow()
    
    private val _events = Channel<UserInfoEvent>()
    val events = _events.receiveAsFlow()
    
    fun onAction(action: UserInfoAction) {
        when (action) {
            is UserInfoAction.OnNameChange -> {
                _state.value = _state.value.copy(
                    name = action.name,
                    nameError = null
                )
            }
            is UserInfoAction.OnAgeChange -> {
                _state.value = _state.value.copy(
                    age = action.age,
                    ageError = null
                )
            }
            is UserInfoAction.OnWeightChange -> {
                _state.value = _state.value.copy(
                    weight = action.weight,
                    weightError = null
                )
            }
            is UserInfoAction.OnContinueClick -> {
                handleContinue()
            }
        }
    }
    
    private fun handleContinue() {
        if (validateInputs()) {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                
                try {
                    val currentUser = authService.currentUser
                    if (currentUser != null) {
                        val userProfile = UserProfile(
                            uid = currentUser.uid,
                            email = currentUser.email ?: "",
                            displayName = currentUser.displayName ?: "",
                            name = _state.value.name,
                            age = _state.value.age.toIntOrNull() ?: 0,
                            weight = _state.value.weight.toDoubleOrNull() ?: 0.0
                        )
                        
                        when (val result = firestoreService.saveUserProfile(userProfile)) {
                            is AuthResult.Success -> {
                                _state.value = _state.value.copy(isLoading = false)
                                _events.send(UserInfoEvent.NavigateToPokemonSelection)
                            }
                            is AuthResult.Error -> {
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    generalError = result.message
                                )
                            }
                        }
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            generalError = "Error: Usuario no autenticado"
                        )
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        generalError = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }
    
    private fun validateInputs(): Boolean {
        val name = _state.value.name.trim()
        val age = _state.value.age
        val weight = _state.value.weight
        
        var hasErrors = false
        
        // Validar nombre
        if (name.isEmpty()) {
            _state.value = _state.value.copy(nameError = "El nombre es requerido")
            hasErrors = true
        } else if (name.length < 2) {
            _state.value = _state.value.copy(nameError = "El nombre debe tener al menos 2 caracteres")
            hasErrors = true
        }
        
        // Validar edad
        val ageInt = age.toIntOrNull()
        if (age.isEmpty()) {
            _state.value = _state.value.copy(ageError = "La edad es requerida")
            hasErrors = true
        } else if (ageInt == null || ageInt < 10 || ageInt > 100) {
            _state.value = _state.value.copy(ageError = "Ingresa una edad válida (10-100)")
            hasErrors = true
        }
        
        // Validar peso
        val weightDouble = weight.toDoubleOrNull()
        if (weight.isEmpty()) {
            _state.value = _state.value.copy(weightError = "El peso es requerido")
            hasErrors = true
        } else if (weightDouble == null || weightDouble < 30 || weightDouble > 300) {
            _state.value = _state.value.copy(weightError = "Ingresa un peso válido (30-300 kg)")
            hasErrors = true
        }
        
        return !hasErrors
    }
}

data class UserInfoState(
    val name: String = "",
    val age: String = "",
    val weight: String = "",
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val ageError: String? = null,
    val weightError: String? = null,
    val generalError: String? = null
)

sealed class UserInfoAction {
    data class OnNameChange(val name: String) : UserInfoAction()
    data class OnAgeChange(val age: String) : UserInfoAction()
    data class OnWeightChange(val weight: String) : UserInfoAction()
    object OnContinueClick : UserInfoAction()
}

sealed class UserInfoEvent {
    object NavigateToPokemonSelection : UserInfoEvent()
}
