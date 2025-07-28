package com.jimmy.valladares.pokefitcompose.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    
    private val _state = MutableStateFlow(LoginScreenState())
    val state: StateFlow<LoginScreenState> = _state.asStateFlow()
    
    private val _events = Channel<LoginScreenEvent>()
    val events = _events.receiveAsFlow()
    
    fun onAction(action: LoginScreenAction) {
        when (action) {
            is LoginScreenAction.OnEmailChange -> {
                _state.value = _state.value.copy(
                    email = action.email,
                    emailError = null
                )
            }
            is LoginScreenAction.OnPasswordChange -> {
                _state.value = _state.value.copy(
                    password = action.password,
                    passwordError = null
                )
            }
            is LoginScreenAction.OnSignInClick -> {
                handleSignIn()
            }
            is LoginScreenAction.OnTogglePasswordVisibility -> {
                _state.value = _state.value.copy(
                    isPasswordVisible = !_state.value.isPasswordVisible
                )
            }
            is LoginScreenAction.OnForgotPasswordClick -> {
                handleForgotPassword()
            }
            is LoginScreenAction.OnRegisterClick -> {
                handleRegisterNavigation()
            }
            is LoginScreenAction.OnDismissError -> {
                _state.value = _state.value.copy(
                    emailError = null,
                    passwordError = null,
                    generalError = null
                )
            }
        }
    }
    
    private fun handleSignIn() {
        if (validateInputs()) {
            _state.value = _state.value.copy(isLoading = true)
            
            // Simulate login process
            viewModelScope.launch {
                kotlinx.coroutines.delay(2000) // Simular llamada a API
                _state.value = _state.value.copy(isLoading = false)
                _events.send(LoginScreenEvent.NavigateToHome)
            }
        }
    }
    
    private fun handleForgotPassword() {
        viewModelScope.launch {
            _events.send(LoginScreenEvent.NavigateToForgotPassword)
        }
    }
    
    private fun handleRegisterNavigation() {
        viewModelScope.launch {
            _events.send(LoginScreenEvent.NavigateToRegister)
        }
    }
    
    private fun validateInputs(): Boolean {
        val email = _state.value.email.trim()
        val password = _state.value.password
        
        var hasErrors = false
        
        // Validar email
        if (email.isEmpty()) {
            _state.value = _state.value.copy(emailError = "El email es requerido")
            hasErrors = true
        } else if (!isValidEmail(email)) {
            _state.value = _state.value.copy(emailError = "Formato de email inválido")
            hasErrors = true
        } else {
            _state.value = _state.value.copy(emailError = null)
        }
        
        // Validar contraseña
        if (password.isEmpty()) {
            _state.value = _state.value.copy(passwordError = "La contraseña es requerida")
            hasErrors = true
        } else if (password.length < 6) {
            _state.value = _state.value.copy(passwordError = "La contraseña debe tener al menos 6 caracteres")
            hasErrors = true
        } else {
            _state.value = _state.value.copy(passwordError = null)
        }
        
        return !hasErrors
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
