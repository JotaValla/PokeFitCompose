package com.jimmy.valladares.pokefitcompose.presentation.register

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
class RegisterViewModel @Inject constructor() : ViewModel() {
    
    private val _state = MutableStateFlow(RegisterScreenState())
    val state: StateFlow<RegisterScreenState> = _state.asStateFlow()
    
    private val _events = Channel<RegisterScreenEvent>()
    val events = _events.receiveAsFlow()
    
    fun onAction(action: RegisterScreenAction) {
        when (action) {
            is RegisterScreenAction.OnNameChange -> {
                _state.value = _state.value.copy(
                    fullName = action.name,
                    fullNameError = null
                )
            }
            is RegisterScreenAction.OnEmailChange -> {
                _state.value = _state.value.copy(
                    email = action.email,
                    emailError = null
                )
            }
            is RegisterScreenAction.OnPasswordChange -> {
                _state.value = _state.value.copy(
                    password = action.password,
                    passwordError = null
                )
            }
            is RegisterScreenAction.OnConfirmPasswordChange -> {
                _state.value = _state.value.copy(
                    confirmPassword = action.confirmPassword,
                    confirmPasswordError = null
                )
            }
            is RegisterScreenAction.OnAcceptTermsChange -> {
                _state.value = _state.value.copy(
                    acceptTerms = action.accept
                )
            }
            is RegisterScreenAction.OnCreateAccountClick -> {
                handleCreateAccount()
            }
            is RegisterScreenAction.OnTogglePasswordVisibility -> {
                _state.value = _state.value.copy(
                    isPasswordVisible = !_state.value.isPasswordVisible
                )
            }
            is RegisterScreenAction.OnToggleConfirmPasswordVisibility -> {
                _state.value = _state.value.copy(
                    isConfirmPasswordVisible = !_state.value.isConfirmPasswordVisible
                )
            }
            is RegisterScreenAction.OnLoginClick -> {
                handleLoginNavigation()
            }
            is RegisterScreenAction.OnDismissError -> {
                _state.value = _state.value.copy(
                    fullNameError = null,
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    generalError = null
                )
            }
        }
    }
    
    private fun handleCreateAccount() {
        if (validateInputs()) {
            _state.value = _state.value.copy(isLoading = true)
            
            // Simulate registration process
            viewModelScope.launch {
                kotlinx.coroutines.delay(2000) // Simular llamada a API
                _state.value = _state.value.copy(isLoading = false)
                _events.send(RegisterScreenEvent.NavigateToHome)
            }
        }
    }
    
    private fun handleLoginNavigation() {
        viewModelScope.launch {
            _events.send(RegisterScreenEvent.NavigateToLogin)
        }
    }
    
    private fun validateInputs(): Boolean {
        val fullName = _state.value.fullName.trim()
        val email = _state.value.email.trim()
        val password = _state.value.password
        val confirmPassword = _state.value.confirmPassword
        
        var hasErrors = false
        
        // Validar nombre completo
        if (fullName.isEmpty()) {
            _state.value = _state.value.copy(fullNameError = "El nombre completo es requerido")
            hasErrors = true
        } else if (fullName.length < 2) {
            _state.value = _state.value.copy(fullNameError = "El nombre debe tener al menos 2 caracteres")
            hasErrors = true
        } else {
            _state.value = _state.value.copy(fullNameError = null)
        }
        
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
        
        // Validar confirmación de contraseña
        if (confirmPassword.isEmpty()) {
            _state.value = _state.value.copy(confirmPasswordError = "Confirma tu contraseña")
            hasErrors = true
        } else if (password != confirmPassword) {
            _state.value = _state.value.copy(confirmPasswordError = "Las contraseñas no coinciden")
            hasErrors = true
        } else {
            _state.value = _state.value.copy(confirmPasswordError = null)
        }
        
        // Validar términos y condiciones
        if (!_state.value.acceptTerms) {
            _state.value = _state.value.copy(generalError = "Debes aceptar los términos y condiciones")
            hasErrors = true
        } else {
            _state.value = _state.value.copy(generalError = null)
        }
        
        return !hasErrors
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
