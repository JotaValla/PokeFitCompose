package com.jimmy.valladares.pokefitcompose.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.valladares.pokefitcompose.data.auth.AuthResult
import com.jimmy.valladares.pokefitcompose.data.auth.FirebaseAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authService: FirebaseAuthService
) : ViewModel() {
    
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()
    
    private val _events = Channel<ProfileEvent>()
    val events = _events.receiveAsFlow()
    
    init {
        loadUserInfo()
    }
    
    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.OnSignOutClick -> {
                handleSignOut()
            }
        }
    }
    
    private fun loadUserInfo() {
        val currentUser = authService.currentUser
        _state.value = _state.value.copy(
            userEmail = currentUser?.email ?: "",
            userName = currentUser?.displayName ?: "Usuario"
        )
    }
    
    private fun handleSignOut() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            when (val result = authService.signOut()) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _events.send(ProfileEvent.NavigateToWelcome)
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
}

data class ProfileState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val errorMessage: String? = null
)

sealed class ProfileAction {
    object OnSignOutClick : ProfileAction()
}

sealed class ProfileEvent {
    object NavigateToWelcome : ProfileEvent()
}
